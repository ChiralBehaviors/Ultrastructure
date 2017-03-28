package com.chiralbehaviors.CoRE.phantasm.java.generator;

import java.util.Map;

import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetImportContext;

public class ImportedFacet implements Facet {

    private final ScopedName classification;
    private final ScopedName classifier;
    private final String     className;
    private final String     packageName;
    private final String     uri;

    public ImportedFacet(String packageName, FacetImportContext facet,
                         String uri) {
        this.classifier = new ScopedName(facet.classifier);
        this.classification = new ScopedName(facet.classification);
        this.packageName = packageName;
        this.uri = uri;
        this.className = facet.classification.member.getText();
    }

    @Override
    public ScopedName getClassification() {
        return classification;
    }

    @Override
    public ScopedName getClassifier() {
        return classifier;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getImport() {
        return null;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getParameterName() {
        return getClassName();
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void resolve(PhantasmGenerator generator,
                        WorkspacePresentation presentation,
                        Map<ScopedName, MappedAttribute> mapped) {
    }

}
