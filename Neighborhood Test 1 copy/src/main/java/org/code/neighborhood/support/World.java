package org.code.neighborhood.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class World{
  private final Grid grid;

  public World(int size) {
    GridFactory gridFactory = new GridFactory();
    this.grid = gridFactory.createEmptyGrid(size);
  }

  public World(String filePath) {
    try {
        String json = Files.readString(Paths.get(filePath));
        this.grid = new GridFactory().createGridFromString(json);
    } catch (IOException e) {
        throw new RuntimeException("Failed to load grid: " + e.getMessage());
    }
}

  public World() {
    GridFactory gridFactory = new GridFactory();
    try {
      this.grid = gridFactory.createGridFromJSON("grid.txt");
    } catch (IOException e) {
      throw new RuntimeException("Could not load grid");
    }
  }

  public World(String filePath, boolean isFilePath) {
    GridFactory gridFactory = new GridFactory();
    try {
      String json = Files.readString(Paths.get(filePath));
      this.grid = gridFactory.createGridFromString(json);
    } catch (IOException e) {
      throw new RuntimeException("Could not load grid");
    }
  }

  public Grid getGrid() {
    return this.grid;
  }
}
