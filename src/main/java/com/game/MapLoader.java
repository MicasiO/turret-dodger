package com.game;

import static com.raylib.Raylib.*;

import static com.game.Constants.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapLoader {

	List<List<String>> cells = new ArrayList<>();

	// loads each cell in csv map to cells array
	public List<List<String>> loadFile(int currentLevel) {
		String level = "maps/level" + currentLevel + ".csv";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(level);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String cell;
			while ((cell = reader.readLine()) != null) {
				String[] values = cell.split(",");
				cells.add(Arrays.asList(values));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cells;
	}

	// loop through cells and and create entities based on cell values
	public void initiateLevel() {
		int y = 0;
		for (List<String> cell : cells) {
			int x = 0;
			for (String value : cell) {
				if (value == null || value.isEmpty()) {
					x++;
					continue;
				}
				// create turret
				if (value.charAt(0) == 't') {
					float[] turretInfo = getTurretInfo(value);

					addTurret(x * BLOCK_SIZE, y * BLOCK_SIZE, turretInfo[0],
							turretInfo[1], turretInfo[2]);
				}

				// create wall
				if (value.equals("w") || value.charAt(0) == 't') {
					addWall(x * BLOCK_SIZE, y * BLOCK_SIZE);
				}

				// set player starting position
				if (value.equals("p")) {
					World.get().starterPos.x(x * BLOCK_SIZE);
					World.get().starterPos.y(y * BLOCK_SIZE);
				}

				// create floor
				if (value.equals("0") || value.equals("p")) {
					Rectangle floor = new Rectangle();
					floor.x(x * BLOCK_SIZE).y(y * BLOCK_SIZE).width(BLOCK_SIZE).height(BLOCK_SIZE);
					World.get().floors.add(floor);
				}

				// create level end door
				if (value.equals("e")) {
					World.get().levelEnd.x(x * BLOCK_SIZE).y(y * BLOCK_SIZE).width(BLOCK_SIZE).height(BLOCK_SIZE);
				}

				x++;
			}
			y++;
		}
	}

	// get turret arguments from csv cell. info about arguments in
	// "mapdefinitions.txt"
	float[] getTurretInfo(String v) {
		int firstOpen = v.indexOf('[');
		int firstClose = v.indexOf(']', firstOpen);
		int secondOpen = v.indexOf('[', firstClose);
		int secondClose = v.indexOf(']', secondOpen);
		int thirdOpen = v.indexOf('[', secondClose);
		int thirdClose = v.indexOf(']', thirdOpen);

		float rot = Float.parseFloat(v.substring(firstOpen + 1, firstClose));
		float interval = Float.parseFloat(v.substring(secondOpen + 1, secondClose));
		float speed = Float.parseFloat(v.substring(thirdOpen + 1, thirdClose));

		return new float[] { rot, interval, speed };
	}

	public void addTurret(int x, int y, float rot, float interval, float speed) {
		Turret t = new Turret();
		t.rect.x(x).y(y).width(BLOCK_SIZE)
				.height(BLOCK_SIZE);
		t.rot = rot;
		t.interval = (float) interval;
		t.speed = speed;
		World.get().turrets.add(t);

	}

	public void addWall(int x, int y) {
		Rectangle rect = new Rectangle();
		rect.x(x).y(y).width(BLOCK_SIZE)
				.height(BLOCK_SIZE);
		World.get().walls.add(rect);
	}

}
