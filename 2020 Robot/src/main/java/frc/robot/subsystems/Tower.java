/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Tower extends SubsystemBase {
  /**
   * Creates a new Tower.
   */

  //sets instance as a new Tower, and will return that when called
  private static Tower instance = null;
  public static Tower getInstance() {
    if (instance == null) {
      instance = new Tower();
    }
    return instance;
  }

  private TalonSRX mTowerMotor;

  private DigitalInput mBeamBreakOne;
  private DigitalInput mBeamBreakTwo;
  private DigitalInput mBeamBreakThree;
  private DigitalInput mBeamBreakFour;

  /**
   * sets a CANSparkMax controller for the Tower motor, and a DI, which detects the state of the Beam Break
   */
  public Tower() {
    // Setup the tower motor (1x 775 Pro)
    mTowerMotor = new TalonSRX(Constants.Tower.TOWER_MOTOR_PORT);

    // Configure the tower motor
    mTowerMotor.configPeakCurrentLimit(Constants.Sorter.SORTER_CURRENT_LIMIT, Constants.TIMEOUT_MS);
    mTowerMotor.enableCurrentLimit(false);
    mTowerMotor.setNeutralMode(NeutralMode.Brake);
    mTowerMotor.configOpenloopRamp(0.5);
    mTowerMotor.setInverted(true);

    // Create the beam breaks through the ball path (4x 5mm IR Beam Break from Adafruit)
    mBeamBreakOne = new DigitalInput(Constants.Tower.BEAM_BREAK_ONE_PORT);
    mBeamBreakTwo = new DigitalInput(Constants.Tower.BEAM_BREAK_TWO_PORT);
    mBeamBreakThree = new DigitalInput(Constants.Tower.BEAM_BREAK_THREE_PORT);
    mBeamBreakFour = new DigitalInput(Constants.Tower.BEAM_BREAK_FOUR_PORT);
  }
  
  /** 
   * sets the Tower motor to the speed defined in constants
   */
  public void run() {
    mTowerMotor.set(ControlMode.PercentOutput, Constants.Tower.RUN_TOWER_MOTOR_SPEED);
  }

  /** 
   * sets the Tower motor to reverse the speed defined in constants
   */
  public void runBackwards() {
    mTowerMotor.set(ControlMode.PercentOutput, -1.0 * Constants.Tower.RUN_TOWER_MOTOR_SPEED);
  }

  public void setTowerSpeed(double speed) {
    mTowerMotor.set(ControlMode.PercentOutput, speed);
  }

  /**
   * gets the double value of the tower motor speed
   * @return the output of the motor controller percentage
   */
  public double getTowerMotorSpeed() {
    return mTowerMotor.getMotorOutputPercent();
  }

  /**
   * gets the boolean value of the beam break state
   * @return beam state, true = beam is broken
   */
  public boolean getBeamBreakOneState(){
    return !mBeamBreakOne.get();
  }

  /**
   * gets the boolean value of the beam break state
   * @return beam state, true = beam is broken
   */
  public boolean getBeamBreakTwoState(){
    return !mBeamBreakTwo.get();
  }

  /**
   * gets the boolean value of the beam break state
   * @return beam state, true = beam is broken
   */
  public boolean getBeamBreakThreeState(){
    return !mBeamBreakThree.get();
  }

  /**
   * gets the boolean value of the beam break state
   * @return beam state, true = beam is broken
   */
  public boolean getBeamBreakFourState(){
    return !mBeamBreakFour.get();
  }
  
  public void logToDashboard() {
    // SmartDashboard.putNumber("Tower/Tower Motor Speed", getTowerMotorSpeed());
    SmartDashboard.putBoolean("Tower/Tower Beam Break One State", getBeamBreakOneState());
    SmartDashboard.putBoolean("Tower/Tower Beam Break Two State", getBeamBreakTwoState());
    SmartDashboard.putBoolean("Tower/Tower Beam Break Three State", getBeamBreakThreeState());
    SmartDashboard.putBoolean("Tower/Tower Beam Break Four State", getBeamBreakFourState());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    logToDashboard();
  }
}
