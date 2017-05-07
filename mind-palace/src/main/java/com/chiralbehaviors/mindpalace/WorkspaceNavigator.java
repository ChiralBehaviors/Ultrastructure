package com.chiralbehaviors.mindpalace;

import processing.core.PApplet;
import queasycam.QueasyCam;

public class WorkspaceNavigator extends PApplet {
	
	
	private QueasyCam cam;
	
	public static void main(String[] args) {
        PApplet.main(WorkspaceNavigator.class.getName());

    }
	
	@Override
	public void settings() {
		size(400, 400, P3D);
	}

	@Override
	public void setup() {

		cam = new QueasyCam(this);
		cam.speed = 5;              // default is 3
		cam.sensitivity = (float) 0.5;      // default is 2
	}

	@Override
	public void draw() {
		background(0);
		box(200);
	}

}
