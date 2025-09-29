package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

public class Bullet {

	boolean active = true;
	int size = 13;
	float speed;
	float rot;

	Vector2 rotatedDir = new Vector2();

	Vector2 pos = new Vector2();
	Rectangle rect = new Rectangle();

	public Bullet(float x, float y, float r, float s) {
		pos.x(x - size / 2).y(y - size / 2);
		rot = r;
		speed = s;
		Vector2 dir = new Vector2();
		dir.x(1).y(0);

		rotatedDir = Vector2Rotate(dir, (float) Math.toRadians(rot * 90 - 90));
	}

	public void draw() {
		DrawRectangle((int) pos.x(), (int) pos.y(), size, size, YELLOW);
	}

	public void update() {
		pos.x(pos.x() + (rotatedDir.x() * speed));
		pos.y(pos.y() + (rotatedDir.y() * speed));

		rect.x(pos.x()).y(pos.y()).width(size).height(size);

		checkCollisions();
	}

	void checkCollisions() {
		if ((World.get().player != null) && !World.get().player.dead) {
			if (CheckCollisionCircleRec(World.get().player.pos, World.get().player.radius, rect)) {
				World.get().player.kill();
				active = false;
			}
		}

		for (Rectangle wall : World.get().walls) {
			if (CheckCollisionRecs(wall, rect)) {
				active = false;
			}
		}
	}
}
