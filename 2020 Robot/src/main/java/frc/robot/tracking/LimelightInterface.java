/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.tracking;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * Add your docs here.
 */
public class LimelightInterface extends SubsystemBase {
    private static LimelightInterface mInstance;

    public static LimelightInterface getInstance() {
      if (mInstance == null)
        mInstance = new LimelightInterface();
      return mInstance;
    }

    private final double LENGTH = 2.5;
    private final double HEIGHT = 7.458333333;
    private final double LIMELIGHT_HEIGHT = 1.833333;
    public double tv;
    public double tx;
    public double ty;
    public double ta;
    private double ts;
    private double tl;
    private double tshort;
    private double tlong;
    private double thor;
    private double tvert;
    private double distanceToGoal;
    private double angleToGoal;

    private Integer angleOnGoalCount = 0;

    public LimelightInterface() {
        tv = 0.0;
        tx = 0.0;
        ty = 0.0;
        ta = 0.0;
        tlong = 0.0;
        tshort = 0.0;
        distanceToGoal = 0.0;
        angleToGoal = 0.0;
        angleOnGoalCount = 0;
    }

    /**
     * This is the method for pulling values from LimeLight
     * This is called in the Robot Periodic Method
     */
    public void run() {
        tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
        ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);
        tlong = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tlong").getDouble(0);
        tshort = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tshort").getDouble(0);
        calculateDistance();
        calculateAngle();
        logToDashBoard();

        if (Math.abs(tx) <= Constants.Drive.ANGLE_TOLERANCE) {
            angleOnGoalCount++;
        } else {
            angleOnGoalCount = 0;
        }
    }

    /**
     * Calculates the Distance of the robot from the goal
     */
    public void calculateDistance() {
        double w = 320.0, a = 59.6, b = 49.7, distMultiplier = 1.0 / 10.0;
        // distanceToGoal = ((LENGTH * w)/(2.0 * tlong * Math.tan(Math.toRadians(b / 2.0))));
        //distanceToGoal = (HEIGHT - LIMELIGHT_HEIGHT) / Math.tan((ty + (b / 2.0)) * (Math.PI / 180.0));
        distanceToGoal = ((HEIGHT - LIMELIGHT_HEIGHT) / Math.tan(Math.toRadians(ty + 30.0))) * 12.0;
        // distanceToGoal += distanceToGoal * distMultiplier;
    }

    /**
     * Calculates the angle of the robot from the goal
     */
    public void calculateAngle() {
        // double angleMultiplier = 29.8 / 320.0; // degrees/pixel
        angleToGoal = tx; //*angleToGoal;
    }

    /**
     * Sets the LED State of the LimeLight
     * @param ledState - 1: force off 2: force blink 3: force on
     */
    public void setLimeLightLED(int ledState) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(ledState);
    }

    /**
     * Gets the calculated distance from the goal
     * @return distance
     */
    public double getDistance() {
        return distanceToGoal;
    }

    /**
     * Method that logs values to dashboard
     */
    public void logToDashBoard() {
        SmartDashboard.putNumber("Vision/tv", tv);
        //System.out.println("tv: " + tv);
        SmartDashboard.putNumber("Vision/tx", tx);
        //System.out.println("tx: " + tx);
        SmartDashboard.putNumber("Vision/ty", ty);
        //System.out.println("ty: " + ty);
        SmartDashboard.putNumber("Vision/ta", ta);
        //System.out.println("ta: " + ta);
        SmartDashboard.putNumber("Vision/tlong", tlong);
        //System.out.println("tlong: " + tlong);
        SmartDashboard.putNumber("Vision/Distance To Goal", distanceToGoal);
        SmartDashboard.putNumber("Vision/Angle To Goal", angleToGoal);
        // System.out.println("Distance To Goal: " + distanceToGoal);
    }

    /**
     * returns if there is a target detected by the limelight
     */
    public boolean hasTarget(){
        return tv > 0;
    }

    /**
     * returns if the robot is aligned with the target
     */
    public boolean alignedToGoal() {
        return hasTarget() && Math.abs(angleToGoal) <= Constants.Drive.ANGLE_TOLERANCE  && angleOnGoalCount >=7;
    }
}
