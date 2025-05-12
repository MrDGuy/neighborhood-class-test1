package org.code.neighborhood;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.code.neighborhood.gui.PainterVisualizer;
import org.code.neighborhood.support.ColorHelpers;
import org.code.neighborhood.support.Direction;
import org.code.neighborhood.support.ExceptionKeys;
import org.code.neighborhood.support.Grid;
import org.code.neighborhood.support.GridSquare;
import org.code.neighborhood.support.NeighborhoodRuntimeException;
import org.code.neighborhood.support.World;

public class Painter {
  private static final int LARGE_GRID_SIZE = 20;
  private static String DIRECTION;
  //private static Color COLOR;
  private static final String ID = "id";
  private static int lastId = 0;
  private int xLocation;
  private int yLocation;
  private Direction direction;
  private int remainingPaint;
  private final boolean hasInfinitePaint;
  private final Grid grid;
  private final String id;
  private PainterVisualizer visualizer;
  private int delay;

  /** Creates a Painter object at (0, 0), facing East, with no paint. */
  public Painter() {
    this(0, 0, "east", 0, true, "resources/my-map.json");
  }

  /**
   * Creates a Painter object
   *
   * @param x the x location of the painter on the grid
   * @param y the y location of the painter on the grid
   * @param direction the direction the painter is facing
   * @param paint the amount of paint the painter has to start
   */
  public Painter(int x, int y, String direction, int paint) {
    this(x, y, direction, paint, false, "resources/my-map.json");
  }

  private Painter(int x, int y, String direction, int paint, boolean couldHaveInfinitePaint, String filePath) {
    this.xLocation = x;
    this.yLocation = y;
    this.direction = Direction.fromString(direction);
    this.remainingPaint = paint;
    this.delay = 300;
    World currentWorld = new World(filePath);
    // if (currentWorld == null) {
    //   currentWorld = new World();
    //   //JavabuilderContext.getInstance().register(World.class, currentWorld);
    // }
    this.grid = currentWorld.getGrid();
    //this.outputAdapter = JavabuilderContext.getInstance().getGlobalProtocol().getOutputAdapter();
    int gridSize = this.grid.getSize();
    this.hasInfinitePaint = couldHaveInfinitePaint ? this.grid.getSize() >= LARGE_GRID_SIZE : false;
    if (x < 0 || y < 0 || x >= gridSize || y >= gridSize) {
      throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_LOCATION);
    }
    this.id = "painter-" + lastId++;
    System.out.println("Painter made with " + this.getMyPaint() + " buckets of paint");
  }

  /** Turns the painter one compass direction left (i.e. North -> West). */
  public void turnLeft() {
    this.direction = this.direction.turnLeft();
    HashMap<String, String> details = this.getSignalDetails();
    details.put(DIRECTION, this.direction.getDirectionString());
    System.out.println("Action: TURN, direction = " + this.direction.getDirectionString());
    repaintWithDelay(delay);
  }

  /** Move the painter one square forward in the direction the painter is facing. */
  public void move() {
    if (this.isValidMovement(this.direction)) {
      if (this.direction.isNorth()) {
        this.yLocation--;
      } else if (this.direction.isSouth()) {
        this.yLocation++;
      } else if (this.direction.isEast()) {
        this.xLocation++;
      } else {
        this.xLocation--;
      }
    } else {
      throw new NeighborhoodRuntimeException(ExceptionKeys.INVALID_MOVE);
    }
    HashMap<String, String> details = this.getSignalDetails();
    details.put(DIRECTION, this.direction.getDirectionString());
    System.out.println("Action: MOVE, direction = " + this.direction.getDirectionString());
    repaintWithDelay(delay);
  }

  /**
   * Add paint to the grid at the painter's location.
   *
   * @param color the color of the paint being added
   */
  public void paint(Color color) {
    if (color == null) {
        throw new IllegalArgumentException("Invalid color: null");
    }

    if (this.hasPaint()) {
        this.grid.getSquare(this.xLocation, this.yLocation).setColor(color);
        this.remainingPaint--;
        System.out.println("Painted with color: " + color.toString());
    } else {
        throw new IllegalStateException("Painter is out of paint.");
    }
    repaintWithDelay(delay);
  }

  public void paint(String colorName) {
    Color color = ColorHelpers.fromName(colorName);
    if (color == null) {
        throw new IllegalArgumentException("Unrecognized color name: " + colorName);
    }
    paint(color); // delegate to the real method
  }

  /** Removes all paint on the square where the painter is standing. */
  public void scrapePaint() {
    this.grid.getSquare(this.xLocation, this.yLocation).removePaint();
    System.out.println("Action: REMOVING PAINT = " + this.direction.getDirectionString());
    repaintWithDelay(delay);
  }

  /**
   * Returns how many units of paint are in the painter's personal bucket.
   *
   * @return the units of paint in the painter's bucket
   */
  public int getMyPaint() {
    return this.remainingPaint;
  }

  /** Hides the painter on the screen. */
  public void hidePainter() {
    System.out.println("Action: HIDING PAINTER");
  }

  /** Shows the painter on the screen. */
  public void showPainter() {
    System.out.println("Action: SHOWING PAINTER");
  }

  public void setDelay(int delay){
    this.delay = delay;
  }

  private void repaintWithDelay(int ms) {
    try {
        // Trigger a repaint of the visualizer
        SwingUtilities.invokeLater(() -> visualizer.repaint());
        Thread.sleep(ms);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
  }

  public void setVisualizer(PainterVisualizer visualizer) {
    this.visualizer = visualizer;
  }

  /**
   * The Painter adds a single unit of paint to their personal bucket. The counter on the bucket on
   * the screen goes down. If the painter is not standing on a paint bucket, nothing happens.
   */
  public void takePaint() {
    GridSquare currentSquare = this.grid.getSquare(this.xLocation, this.yLocation);
    if (currentSquare.containsPaint()) {
        currentSquare.collectPaint();
        this.remainingPaint++;
        System.out.println("Action: TAKING PAINT = " + this.getMyPaint() + " buckets in inventory");

        // Check if no paint buckets remain on the tile
        if (!currentSquare.containsPaint()) {
            currentSquare.setAssetID(0); // Reset assetID to 0
        }
    } else {
        System.out.println("There is no paint to collect here");
    }
    repaintWithDelay(delay);
}

  /** @return True if there is paint in the square where the painter is standing. */
  public boolean isOnPaint() {
    boolean isOnPaint = this.grid.getSquare(this.xLocation, this.yLocation).hasColor();
    System.out.println("Action: IS ON PAINT = " + isOnPaint);
    return isOnPaint;
  }

  /** @return True if there is a paint bucket in the square where the painter is standing. */
  public boolean isOnBucket() {
    boolean isOnBucket = this.grid.getSquare(this.xLocation, this.yLocation).containsPaint();
    System.out.println("Action: IS ON BUCKET = " + isOnBucket);
    return isOnBucket;
  }

  /** @return True if the painter's personal bucket has paint in it. */
  public boolean hasPaint() {
    if (this.hasInfinitePaint) {
      return true;
    }
    return this.remainingPaint > 0;
  }

  /** @return True if there is no barrier one square ahead in the requested direction. */

  public boolean canMove(String relativeDirection) {
    Direction absoluteDirection = getAbsoluteDirection(relativeDirection);
    System.out.println("Action: CAN MOVE " + relativeDirection + ": " + isValidMovement(absoluteDirection));
    return isValidMovement(absoluteDirection);
  }

  private Direction getAbsoluteDirection(String relativeDirection) {
    switch (relativeDirection.toLowerCase()) {
        case "left" -> {
            return direction.left();
          }
        case "right" -> {
            return direction.right();
          }
        case "forward" -> {
            return direction;
          }
        case "backward" -> {
            return direction.opposite();
          }
        default -> throw new IllegalArgumentException("Invalid direction: " + relativeDirection);
    }
  }
  // /** @return True if there is no barrier one square ahead in the requested direction. */
  // public boolean canMove(String direction) {
  //   boolean canMove = this.isValidMovement(Direction.fromString(direction));
  //   System.out.println("Action CAN MOVE: " + direction);
  //   return canMove;
  // }

  /** @return True if there is no barrier one square ahead in the current direction. */
  public boolean canMove() {
    return this.canMove("forward");
  }

  /** @return the color of the square where the painter is standing. */
  public Color getColor() {
    return this.grid.getSquare(this.xLocation, this.yLocation).getColor();
  }

  /** @return True if facing North */
  public boolean isFacingNorth() {
    return this.direction.isNorth();
  }

  /** @return True if facing East */
  public boolean isFacingEast() {
    return this.direction.isEast();
  }

  /** @return True if facing South */
  public boolean isFacingSouth() {
    return this.direction.isSouth();
  }

  /** @return True if facing West */
  public boolean isFacingWest() {
    return this.direction.isWest();
  }

  /** @return the x coordinate of the painter's current position */
  public int getX() {
    return this.xLocation;
  }

  /** @return the y coordinate of the painter's current position */
  public int getY() {
    return this.yLocation;
  }

  /** @return the current direction the painter is facing */
  public String getDirection() {
    return this.direction.getDirectionString();
  }

  public void showBuckets() {
    System.out.println("[Painter] showBuckets() called");
  }

  public void hideBuckets() {
    System.out.println("[Painter] hideBuckets() called");
  }

  /**
   * Sets the amount of paint in the painters bucket. Does nothing if paint is negative.
   *
   * @param paint the amount of paint that should be in the painter's bucket.
   */
  public void setPaint(int paint) {
    if (paint < 0) {
      System.out.println("Paint amount must not be a negative number.");
      return;
    }

    if (this.hasInfinitePaint) {
      return;
    }

    this.remainingPaint = paint;
  }

  /**
   * Helper function to check if the painter can move in the specified direction.
   *
   * @param movementDirection the direction of movement
   * @return True if the painter can move in that direction
   */
  private boolean isValidMovement(Direction movementDirection) {
    if (movementDirection.isNorth()) {
      return this.grid.validLocation(this.xLocation, this.yLocation - 1);
    } else if (movementDirection.isSouth()) {
      return this.grid.validLocation(this.xLocation, this.yLocation + 1);
    } else if (movementDirection.isEast()) {
      return this.grid.validLocation(this.xLocation + 1, this.yLocation);
    } else {
      return this.grid.validLocation(this.xLocation - 1, this.yLocation);
    }
  }

  private HashMap<String, String> getSignalDetails() {
    HashMap<String, String> details = new HashMap<>();
    details.put(ID, this.id);
    return details;
  }

  public Grid getGrid() {
    return this.grid;
  }

  // private void sendOutputMessage(NeighborhoodSignalKey signalKey, HashMap<String, String> details) {
  //   this.outputAdapter.sendMessage(new NeighborhoodSignalMessage(signalKey, details));
  // }

  // private void sendBooleanMessage(NeighborhoodSignalKey signalKey, boolean result) {
  //   HashMap<String, String> details = this.getSignalDetails();
  //   String resultString = String.valueOf(result);
  //   details.put(BOOLEAN_RESULT, resultString);
  //   this.sendOutputMessage(signalKey, details);
  // }

  // private void sendInitializationMessage() {
  //   HashMap<String, String> initDetails = this.getSignalDetails();
  //   initDetails.put(DIRECTION, this.direction.getDirectionString());
  //   initDetails.put(X, Integer.toString(this.xLocation));
  //   initDetails.put(Y, Integer.toString(this.yLocation));
  //   initDetails.put(PAINT, Integer.toString(this.remainingPaint));
  //   this.sendOutputMessage(NeighborhoodSignalKey.INITIALIZE_PAINTER, initDetails);
  // }
}
