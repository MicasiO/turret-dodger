package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import static com.game.Constants.*;

import java.util.ArrayList;
import java.util.List;

public class Turret {

	Texture spriteSheet;
	int frameCount = 5;
	int frameWidth;
	int frameHeight;

	int currentFrame = 0;
	float frameTime = 0.04f;
	float timer = 0;

	Rectangle rect = new Rectangle();
	float interval;
	float rot;
	float speed;
	boolean animating = false;
	float endTime = 0;
	float lastShotTime = 0;
	float currentTime;

	List<Bullet> bullets = new ArrayList<>();

	public Turret() {
		spriteSheet = LoadTexture("assets/animations/obstacles/turret.png");
		frameWidth = spriteSheet.width() / frameCount;
		frameHeight = spriteSheet.height();

	}

	void draw(float scale) {
		// draw bullets
		for (Bullet b : bullets) {
			b.draw();
		}

		Rectangle sourceRec = new Rectangle();
		sourceRec.x(frameWidth * currentFrame)
				.y(0)
				.width(frameWidth)
				.height(frameHeight);

		Rectangle destRec = new Rectangle();
		destRec.x(rect.x() + frameWidth / 2.0f * scale)
				.y(rect.y() + frameWidth / 2.0f * scale)
				.width(frameWidth * scale)
				.height(frameHeight * scale);

		Vector2 origin = new Vector2();
		origin.x(frameWidth / 2.0f * scale).y(frameWidth / 2.0f * scale);

		DrawTexturePro(spriteSheet, sourceRec, destRec, origin, rot * 90 + 180, WHITE);
	}

	public void update() {
		currentTime = (float) GetTime();
		if (!animating && currentTime - lastShotTime >= interval) {
			animating = true;
			currentFrame = 0;
			timer = 0;
			lastShotTime = currentTime;
			shootBullet();
		}
		updateAnimation();
		updateBullets();

		// update bullets
		for (Bullet b : bullets) {
			b.update();
		}
	}

	void shootBullet() {
		Vector2 baseDir = new Vector2();
		baseDir.x(2).y(0);

		Vector2 dir = Vector2Rotate(baseDir, (float) Math.toRadians(rot * 90 - 90));

		float offset = BLOCK_SIZE / 2.0f;
		float bx = rect.x() + BLOCK_SIZE / 2.0f + dir.x() * offset;
		float by = rect.y() + BLOCK_SIZE / 2.0f + dir.y() * offset;
		Bullet b = new Bullet(bx, by, rot, speed);
		bullets.add(b);
	}

	void updateAnimation() {
		if (animating) {
			timer += GetFrameTime();
			if (timer >= frameTime) {
				timer = 0;
				currentFrame++;
				if (currentFrame >= frameCount) {
					currentFrame = 0;
					animating = false;
				}
			}
		}
	}

	void updateBullets() {
		bullets.removeIf(b -> !b.active);
	}
}
