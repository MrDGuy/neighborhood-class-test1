package org.code.neighborhood.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GridFactory {
  private static final String GRID_FILE_NAME = "grid.txt";
  private static final String GRID_SQUARE_TYPE_FIELD = "tileType";
  private static final String GRID_SQUARE_ASSET_ID_FIELD = "assetId";
  private static final String GRID_SQUARE_VALUE_FIELD = "value";

  protected GridFactory() {}

  protected Grid createGridFromJSON(String filename) throws IOException {
    File file = new File(GRID_FILE_NAME);
    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
      byte[] data = new byte[(int) file.length()];
      fis.read(data);
      fis.close();
      return createGridFromString(new String(data, "UTF-8"));
    } catch (IOException e) {
      throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_GRID);
    }
  }

  // Creates a grid from a string, assuming that the string is a 2D JSONArray of JSONObjects
  // with each JSONObject containing an integer tileType and optionally an integer value
  // corresponding with the paintCount for that tile.
  protected Grid createGridFromString(String description) {
    try {
      JsonArray gridSquares = JsonParser.parseString(description).getAsJsonArray();
      int height = gridSquares.size();
      if (height == 0) {
        throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_GRID);
      }
  
      int width = gridSquares.get(0).getAsJsonArray().size();
      if (width != height) {
        throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_GRID);
      }
  
      GridSquare[][] grid = new GridSquare[height][width];
      //System.out.println("Grid size: " + width + "x" + height);
  
      for (int currentY = 0; currentY < height; currentY++) {
        JsonArray line = gridSquares.get(currentY).getAsJsonArray();
        if (line.size() != width) {
          throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_GRID);
        }
  
        for (int currentX = 0; currentX < line.size(); currentX++) {
          JsonObject descriptor = line.get(currentX).getAsJsonObject();
          int tileType = descriptor.get(GRID_SQUARE_TYPE_FIELD).getAsInt();
          int assetId = descriptor.has(GRID_SQUARE_ASSET_ID_FIELD)
              ? descriptor.get(GRID_SQUARE_ASSET_ID_FIELD).getAsInt()
              : 0;
  
          if (descriptor.has(GRID_SQUARE_VALUE_FIELD)) {
            int value = descriptor.get(GRID_SQUARE_VALUE_FIELD).getAsInt();
            grid[currentY][currentX] = new GridSquare(tileType, assetId, value);
          } else {
            grid[currentY][currentX] = new GridSquare(tileType, assetId);
          }
        }
      }
  
      return new Grid(grid);
    } catch (Exception e) {
      throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_GRID);
    }
  }
  // Creates an empty size x size grid with every square being open
  // and having assetId 0.
  protected Grid createEmptyGrid(int size) {
    GridSquare[][] grid = new GridSquare[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        grid[i][j] = new GridSquare(1, 0);
      }
    }
    return new Grid(grid);
  }
}
