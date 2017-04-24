package com.chiralbehaviors.mindpalace;

import processing.core.PApplet;

public class WorkspaceNavigator extends PApplet {
	
	public static void main(String[] args) {
        PApplet.main(WorkspaceNavigator.class.getName());

    }
	
	@Override
	public void settings() {
		size(480, 120);
	}

	@Override
	public void setup() {
	}

	@Override
	public void draw() {
		if (mousePressed) {
			fill(0);
		} else {
			fill(255);
		}
		ellipse(mouseX, mouseY, 80, 80);
	}

}
