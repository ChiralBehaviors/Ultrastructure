/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public final class Util {

    public static String md5Hash(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        return md5Hash(new ByteArrayInputStream(bytes));
    }

    /**
     * Generates an MD5 hash of the given File.
     *
     * @param file
     *            the file to hash
     * @return a 32-character hexadecimal string that is the MD5 hash of the
     *         given File's contents, or <code>null</code> if the given File has
     *         is <code>null</code>.
     * @throws IOException
     *             if there is a problem reading the file
     */
    public static String md5Hash(File file) throws IOException {
        if (file == null) {
            return null;
        }
        FileInputStream fis = new FileInputStream(file);
        try {
            return md5Hash(fis);
        } finally {
            fis.close();
        }
    }

    /**
     * Generates an MD5 hash of the given byte array. Uses
     * {@link java.security.MessageDigest}.
     *
     * @param is
     *            the input stream to hash
     * @return a 32-character hexadecimal string that is the MD5 hash of the
     *         given byte array, or <code>null</code> if the given String is
     *         <code>null</code>.
     * @throws IOException
     */
    public static String md5Hash(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        byte[] buffer = new byte[4096];
        for (int read = is.read(buffer); read != -1; read = is.read(buffer)) {
            md.update(buffer, 0, read);
        }
        byte[] raw = md.digest();

        String digestString = digestString(raw);
        return digestString;
    }

    /**
     * Generates an MD5 hash of the given String. Useful for hashing passwords
     * and such.
     *
     * @param toEncrypt
     *            the String to hash
     * @return a 32-character hexadecimal string that is the MD5 hash of the
     *         given String, or <code>null</code> if the given String is
     *         <code>null</code>.
     * @throws IOException
     */
    public static String md5Hash(String toEncrypt) throws IOException {
        String hash = toEncrypt == null ? null : md5Hash(toEncrypt.getBytes());
        return hash;
    }

    public static <T extends Ruleform> T smartMerge(EntityManager em, T ruleform) {
        return map(em, ruleform, new HashMap<>(2048));
    }

    /**
     * We initially used this code to convert the digest to a hex string:
     *
     * return new BigInteger( 1, md.digest( bytes )).toString( 16 );
     *
     * This was a case of being too clever.
     *
     * This approach doesn't always work reliably; you could get hashes that
     * were less than 32 characters long! This is because a full, 32-character
     * MD5 hash considers leading zeros to be significant. If you have a byte
     * whose value is between 0 and 15 and you convert it to hexadecimal, you
     * get only one character. This implementation avoids the issue by checking
     * to see if the result of Integer.toHexString on a byte of the hash is one
     * character long; if so, it left-pads it with a single zero.
     */
    private static String digestString(byte[] raw) {
        StringBuffer sb = new StringBuffer();
        for (byte element : raw) {
            // Convert a byte into a (potentially) two character hexadecimal
            // digit
            String hex = Integer.toHexString(0xff & element);
            // If the byte represented 0 through 15, it's going to be a single
            // digit; pad it!
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }

        String digestString = sb.toString();
        return digestString;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Ruleform> T map(EntityManager em, T ruleform,
                                                Map<Ruleform, Ruleform> mapped) {
        if (mapped.containsKey(ruleform)) {
            //            System.out.println(String.format("%% already mapped: %s",
            //                                             ruleform.getClass().getSimpleName()));
            return (T) mapped.get(ruleform);
        }

        Ruleform reference = find(em, ruleform);
        if (reference != null) {
            //            System.out.println(String.format("%% found: %s",
            //                                             ruleform.getClass().getSimpleName()));
            mapped.put(ruleform, reference);
        } else {
            //            System.out.println(String.format("%% traverse before persist: %s",
            //                                             ruleform.getClass().getSimpleName()));
            mapped.put(ruleform, ruleform);
            traverse(em, ruleform, mapped);
            //            System.out.println(String.format("%% persisting: %s",
            //                                             ruleform.getClass().getSimpleName()));
            em.persist(ruleform);
        }

        return (T) mapped.get(ruleform);
    }

    private static <T extends Ruleform> Ruleform find(EntityManager em,
                                                      T ruleform) {
        if (ruleform instanceof WorkspaceAuthorization) {
            TypedQuery<Long> existQueury = em.createNamedQuery(WorkspaceAuthorization.DOES_WORKSPACE_AUTH_EXIST,
                                                               Long.class);
            existQueury.setParameter("id", ruleform.getId());
            if (existQueury.getFirstResult() == 0) {
                return null;
            }
        }
        return em.find(ruleform.getClass(), ruleform.getId());
    }

    protected static void traverse(EntityManager em, Ruleform ruleform,
                                   Map<Ruleform, Ruleform> mapped) {
        if (ruleform instanceof WorkspaceAuthorization) {
            WorkspaceAuthorization auth = (WorkspaceAuthorization) ruleform;
            auth.setDefiningProduct(map(em, auth.getDefiningProduct(), mapped));
            auth.setEntity(map(em, auth.getEntity(), mapped));
            return;
        }
        for (Field field : getInheritedFields(ruleform.getClass())) {
            if (field.getAnnotation(JoinColumn.class) == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Ruleform value = (Ruleform) field.get(ruleform);
                if (value != null && !ruleform.equals(value)) {
                    Ruleform mappedValue = map(em, value, mapped);
                    if (mappedValue == null) {
                        throw new IllegalStateException(
                                                        String.format("%s mapped to null",
                                                                      value));
                    }
                    field.set(ruleform, mappedValue);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                                                String.format("IllegalAccess access foreign key field: %s",
                                                              field), e);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                                                String.format("Illegal mapped value for field: %s",
                                                              field), e);
            }
        }
    }

    private static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private Util() {

    }
}
