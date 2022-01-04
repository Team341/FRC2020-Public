/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.List;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystems.ControlWheel;
import frc.robot.tracking.LimelightInterface;

public class LEDs extends SubsystemBase {
  /**
   * Creates a new LEDs.
   */

  // private static LEDs instance = null;

  // public static LEDs getInstance() {
  //   if (instance == null) {
  //     instance = new LEDs(
  //       Tower.getInstance(), 
  //       Drive.getInstance(), 
  //       Shooter.getInstance(),
  //       Hanger.getInstance(), 
  //       ControlWheel.getInstance(), 
  //       LimelightInterface.getInstance());
  //   }
  //   return instance;
  // }

  // creates LED object
  private AddressableLED mShooterLED;

  // creates addressable LED buffer object for indexing changes
  private AddressableLEDBuffer mShooterLEDBuffer;

  // TODO figure out what these 9 need to represent
  private AddressableLED mDiagnosticLED;
  private AddressableLEDBuffer mDiagnosticLEDBuffer;

  private static Tower mTower;
  private static Drive mDrive;
  private static Shooter mShooter;
  private static Hanger mHanger;
  private static ControlWheel mControlWheel;
  private static LimelightInterface mLimeLight;
  private static PDPLogger mPDP;
  private double count;

  public LEDs(Tower tower, Drive drive, Shooter shooter, Hanger hanger, ControlWheel wheel, LimelightInterface light, PDPLogger pdp) {
    // TODO: make everything right in the world
    // ie.) state machine jawn

    // Store the handles to the other subsystems for use later
    mTower = tower;
    mDrive = drive;
    mShooter = shooter;
    mHanger = hanger;
    mControlWheel = wheel;
    mLimeLight = light;
    mPDP = pdp;
    count = 0.0;

    // Create the Shooter LEDs
    mShooterLED = new AddressableLED(Constants.LEDs.SHOOTER_LED_PORT);
    mShooterLEDBuffer = new AddressableLEDBuffer(Constants.LEDs.SHOOTER_LED_BUFFER);
    mShooterLED.setLength(mShooterLEDBuffer.getLength());

    // Create the diagnostic LEDs
    // mDiagnosticLED = new AddressableLED(Constants.LEDs.DIAGNOSTIC_LED_PORT);
    // mDiagnosticLEDBuffer = new AddressableLEDBuffer(Constants.LEDs.DIAGNOSTIC_LED_BUFFER);
    // mDiagnosticLED.setLength(mDiagnosticLEDBuffer.getLength());
    startShooterLEDs();
  }

  public void setShowPattern() {
    for (var i = 0; i < mShooterLEDBuffer.getLength(); i++) { 
      if (i % 2 == 0)
      {
        mShooterLEDBuffer.setRGB(i, 0, 0, 255);
      }
      else
      {
        mShooterLEDBuffer.setRGB(i, 255, 255, 0);
      }
    }
    mShooterLED.setData(mShooterLEDBuffer);
  }
  
  /**
   * sets the RGB value for all the LEDs
   * @param RGB List of RGB values
   */
  public void setShooterLEDColorForAll(List<Integer> RGB) {
    for (var i = 0; i < mShooterLEDBuffer.getLength(); i++) { 
      mShooterLEDBuffer.setRGB(i, RGB.get(0), RGB.get(1), RGB.get(2));
    }
    mShooterLED.setData(mShooterLEDBuffer);
  }

  // public void setDiagnosticLEDColorForAll(List<Integer> RGB) {
  //   for (var i = 0; i < mDiagnosticLEDBuffer.getLength(); i++) { 
  //     mDiagnosticLEDBuffer.setRGB(i, RGB.get(0), RGB.get(1), RGB.get(2));
  //   }
  //   mDiagnosticLED.setData(mShooterLEDBuffer);
  // }

  public void startShooterLEDs() {
    mShooterLED.start();
    // mDiagnosticLED.start();
  }

  public void stopLEDs() {
    mShooterLED.stop();
    // mDiagnosticLED.stop();
  }
  
  public void setDiagnosticAtPosition(List<Integer> RGB, int position) {
    mDiagnosticLEDBuffer.setRGB(position, RGB.get(0), RGB.get(1), RGB.get(2));
  }

  /**
   * flushes the diagnosticLEDBuffer out to the diagnostic LEDs. Has it's own function call
   * to prevent flooding the channe with color change requests
   */
  public void sendDiagnosticLedData(){
    mDiagnosticLED.setData(mDiagnosticLEDBuffer);
  }

  /**
   * sets the RGB value for the LED's at a certain int position
   * @param RGB List of RGB values
   * @param position
   */
  public void setLEDColorAtPosition(List<Integer> RGB, int position) {
    mShooterLEDBuffer.setRGB(position, RGB.get(0), RGB.get(1), RGB.get(2));
  }

    /**
   * flushes the shooterLEDBuffer out to the diagnostic LEDs. Has it's own function call
   * to prevent flooding the channe with color change requests
   */
  public void sendShooterLedData(){
    mShooterLED.setData(mShooterLEDBuffer);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run

    // The diagnostic LEDs are in the following order:
    //    0) Beam break position 4 (i.e. just before entering shooter), blue if triggered
    //    1) Beam break position 3 (i.e. at bottom of tower), blue if triggered
    //    2) Beam break position 2 (i.e. in the conveyor), blue if triggered
    //    3) Beam break position 1 (i.e. at the entrance of the conveyor), blue if triggered
    //    4) Shooter RPM - Green within threshold, yellow if within 2*THRESHOLD, red otherwise
    //    5) Color Wheel - returns the corresponding color currently detected by color sensor
    //    6) Elevator Heigh/Limit Switches - off down, magenta in manual position, blue - height high, green - height middle, yellow - height low
    //    7) Battery Voltage - Green > 12.0V > yellow > 10.5V > red
    //    8) PDP Current - Green < 80A < Yellow < 100A < Red

    // Bind LEDs to the tower beam break state
    // if (mTower.getBeamBreakOneState()) {
    //   setDiagnosticAtPosition(List.of(0, 0, 255), 0);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 165, 0), 0);
    // }

    // if (mTower.getBeamBreakTwoState()) {
    //   setDiagnosticAtPosition(List.of(0, 0, 255), 1);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 165, 0), 1);
    // }

    // if (mTower.getBeamBreakThreeState()) {
    //   setDiagnosticAtPosition(List.of(0, 0, 255), 2);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 165, 0), 2);
    // }

    // if (mTower.getBeamBreakFourState()) {
    //   setDiagnosticAtPosition(List.of(0, 0, 255), 3);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 165, 0), 3);
    // }

    // if (Math.abs(mShooter.getDifferenceBetweenRPMAndGoal()) <= mShooter.getRPMThreshold()) {
    //   setDiagnosticAtPosition(List.of(0, 255, 0), 4);
    // } else if (Math.abs(mShooter.getDifferenceBetweenRPMAndGoal()) >= 2.0 * mShooter.getRPMThreshold()) {
    //   setDiagnosticAtPosition(List.of(255, 255, 0), 4);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 0, 0), 4);
    // }

    // // mControlWheel.getColorString() {
      
    // // }
    // // TODO: Remaining bindings
    // // the 6th reporting the color the color sensor is detecting (by showing that color)
    // // this should also be turned off if the associated limit switch is being hit
    // // the 7th reporting the state of the two elevator limit switches
    // // Both are hit -> a color, only one -> another, and a third color for if neither is hit

    // // Bind a LED to display the remaining battery voltage
    // if (mPDP.getVoltage() > 12.0) {
    //   setDiagnosticAtPosition(List.of(0, 255, 0), 7);
    // } else if (mPDP.getVoltage() > 10.5) {
    //   setDiagnosticAtPosition(List.of(255, 255, 0), 7);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 0, 0), 7);
    // }

    // // Bind a LED to display the current draw
    // if (mPDP.getTotalCurrent() < 80) {
    //   setDiagnosticAtPosition(List.of(0, 255, 0), 8);
    // } else if (mPDP.getTotalCurrent() < 100) {
    //   setDiagnosticAtPosition(List.of(255, 255, 0), 8);
    // } else {
    //   setDiagnosticAtPosition(List.of(255, 0, 0), 8);
    // }

    // Determine what color to show on the shooter LEDs:
    //    Green   - aligned to goal and rpm on target (i.e. ready to shoot)
    //    Blue    - aligned to goal, rpm is not ready
    //    Magenta - rpm is ready, but not aligned to goal
    //    Yellow  - a vision target is detected
    //    None    - Nothing is ready, no targets detected
    // TODO: maybe wrap this in a check to see if we are actively trying to target
    if (SmartDashboard.getBoolean("Robot Enabled", true)) {
      if (mLimeLight.alignedToGoal() && mShooter.getOnTargetCount() > 3){
        setShooterLEDColorForAll(Constants.LEDs.GREEN_RGB);
      } else if (mLimeLight.alignedToGoal()) {
        setShooterLEDColorForAll(Constants.LEDs.BLUE_RGB);
      } else if (mShooter.getOnTargetCount() > 3) {
        setShooterLEDColorForAll(Constants.LEDs.MAGENTA_RGB);
      } else if (mLimeLight.hasTarget()){
        setShooterLEDColorForAll(Constants.LEDs.YELLOW_RGB);
      } else {
        setShooterLEDColorForAll(Constants.LEDs.OFF_RGB);
      }
    } else {
      setShowPattern();
    }
    // Send the updated colors to the LEDs
    // sendDiagnosticLedData();
    sendShooterLedData();
    // SmartDashboard.putNumber("LED/Periodic Check", count);
    // count++;
  }
}
