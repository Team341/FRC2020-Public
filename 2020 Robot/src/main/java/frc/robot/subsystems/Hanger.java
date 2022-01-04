
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
public class Hanger extends SubsystemBase {
  /**
   * creates a new Hanger
   */
  
  private static Hanger instance = null;
  public static Hanger getInstance() {
    if(instance == null) {
      instance = new Hanger();
    }
    return instance;
  }

  // creates motor objects for the elevator
  private CANSparkMax mElevatorMotor;

  // creates motor objects for the winch
  private CANSparkMax mWinchMotorLeft;
  private CANSparkMax mWinchMotorRight;  

  public static int elevatorPosition;

  public static double kP = 0.2;
  public static double kI = 0.0;
  public static double kD = 0.0;
  public static double kFF = 0.0;
  
  private CANPIDController mElevatorPIDController;
  public Hanger() {
    // Setup the hanger elevator motor (1x Neo550)
    mElevatorMotor = new CANSparkMax(Constants.Hanger.ELEVATOR_MOTOR_PORT, CANSparkMax.MotorType.kBrushless);
    
    // Configure the elevator motors settings
    mElevatorMotor.restoreFactoryDefaults();
    mElevatorMotor.setInverted(true);
    mElevatorMotor.getEncoder().setPositionConversionFactor(Constants.Hanger.ELEVATOR_CONVERSION_FACTOR);
    mElevatorMotor.setSmartCurrentLimit(Constants.Hanger.ELEVATOR_MOTOR_CURRENT_LIMIT);
    mElevatorMotor.setIdleMode(IdleMode.kBrake);
    mElevatorMotor.setSoftLimit(SoftLimitDirection.kForward, (float) 18.5);
    mElevatorMotor.setSoftLimit(SoftLimitDirection.kReverse, (float) 0.0);
    mElevatorMotor.enableSoftLimit(SoftLimitDirection.kForward, false);
    mElevatorMotor.enableSoftLimit(SoftLimitDirection.kReverse, false);

    // Burn the configuration to flash to prevent issues if power is lost while operating
    mElevatorMotor.burnFlash();

    
    mElevatorPIDController = mElevatorMotor.getPIDController();
    mElevatorPIDController.setOutputRange(-1.0, 1.0);
    mElevatorPIDController.setP(kP);
    mElevatorPIDController.setI(kI);
    mElevatorPIDController.setD(kD);
    mElevatorPIDController.setFF(kFF);

    // Setup the hanger winch motors (2x Neo)
    mWinchMotorLeft = new CANSparkMax(Constants.Hanger.WINCH_LEFT_MOTOR_PORT, CANSparkMax.MotorType.kBrushless);
    mWinchMotorRight = new CANSparkMax(Constants.Hanger.WINCH_RIGHT_MOTOR_PORT, CANSparkMax.MotorType.kBrushless);
    
    // Configure the winch motor settings
    mWinchMotorLeft.restoreFactoryDefaults();
    mWinchMotorRight.restoreFactoryDefaults();
    mWinchMotorLeft.getEncoder().setPositionConversionFactor(Constants.Hanger.WINCH_CONVERSION_FACTOR);
    mWinchMotorLeft.setSmartCurrentLimit(Constants.Hanger.WINCH_MOTOR_CURRENT_LIMIT);
    mWinchMotorRight.setSmartCurrentLimit(Constants.Hanger.WINCH_MOTOR_CURRENT_LIMIT);
    mWinchMotorLeft.setIdleMode(IdleMode.kBrake);
    mWinchMotorRight.setIdleMode(IdleMode.kBrake);
    mWinchMotorRight.setInverted(true);
    
    // Restrict backwards motion since there is a ratchet stage preventing motion in that direction
    // mWinchMotorLeft.setOutputRange(0, 1.0);     
    mWinchMotorLeft.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
    mWinchMotorLeft.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, 0);

    // Set the right motor to be slaved to the left motor, but inverts it's direction
    // mWinchMotorRight.follow(mWinchMotorLeft, true);

    // Burn the configuration to flash memory to prevent issues if power is lost while operating
    mWinchMotorLeft.burnFlash();
    mWinchMotorRight.burnFlash();

    //Set the initial elevator position
    elevatorPosition = 1;

    resetEncoders();
  }

  /**
   * Gets the encoder position of elevator
   * @return elevator encoder position
   */
  public double getElevatorEncoderPos() {
    return mElevatorMotor.getEncoder().getPosition();
  }

  /**
   * Use the PID controller to go to height
   * @param height in inches
   */
  public void goToHeight(double height) {
    mElevatorPIDController.setReference(height, ControlType.kPosition);
  }

  /**
   * Gets the encoder position of left winch
   * @return left winch encoder position
   */
  public double getWinchLeftEncoderPos() {
    return mWinchMotorLeft.getEncoder().getPosition();
  }

  /**
   * Checks if elevator is in stowed position
   * @return true = stowed false = up
   */
  public boolean isElevatorStowed() {
    return getHangerHeightInInches() < Constants.Hanger.ELEVATOR_HEIGHT_THRESHOLD;
  }

  /**
   * Gets the encoder position of right winch
   * @return right winch encoder position
   */
  public double getWinchRightEncoderPos() {
    return mWinchMotorRight.getEncoder().getPosition();
  }

  public void resetEncoders() {
    mWinchMotorLeft.getEncoder().setPosition(0.0);
    mWinchMotorLeft.getEncoder().setPosition(0.0);
    mElevatorMotor.getEncoder().setPosition(0.0);
  }

  /**
   * setable winch speeds for both winches
   * @param speed
   */
  public void setWinchSpeed(double speed) {
    mWinchMotorLeft.set(speed);
    mWinchMotorRight.set(speed);
  }

  public void setLeftWinchSpeed(double speed) {
    mWinchMotorLeft.set(speed);
  }
  public void setRightWinchSpeed(double speed) {
    mWinchMotorRight.set(speed);
  }

  /**
   * individually setable left speed for variable rotation of hanger
   * @param speed
   */ 
  public void setElevatorSpeed(double speed) {
    mElevatorMotor.set(speed);
  }

  /**
   * returns the speed double for the left elevator
   */
  public double getElevatorSpeed() {
    return mElevatorMotor.get();
  }

  /**
   * returns the speed double for both winches
   */
  public double getWinchSpeed() {
    return mWinchMotorLeft.get();
  }

  /**
   * get height of the left elevator
   * @return double height in inches
   */
  public double getHangerHeightInInches() {
    return mElevatorMotor.getEncoder().getPosition();
  }

  /**
   * get the distance the winch has spooled
   * @return double height in inches
   */
  public double getWinchHeightInInches() {
    return mWinchMotorLeft.getEncoder().getPosition();
  }

  /**
   * Get elevator position 1-3, 1 low bar, 3 high bar
   * @return Int elevator position
   */
  public int getElevatorPositionNumber() {
    return elevatorPosition;
  }

  /**
   * Set the elevator position 1 - 3, 1 low bar, 3 high bar
   * @param position Int elevator position
   */
  public void setElevatorPosition(int position) {
    elevatorPosition = position;
    if (elevatorPosition > 3) {
      elevatorPosition = 1;
    }
  }

  public void disableWinchSoftLimits() {
    mWinchMotorLeft.enableSoftLimit(SoftLimitDirection.kReverse, false);
  }

  public void enableWinchSoftLimits() {
    mWinchMotorLeft.enableSoftLimit(SoftLimitDirection.kReverse, true);
  }


  /**
   * logs values to dashboard
   */
  public void logToDashboard(){
    // SmartDashboard.putNumber("Hanger/ElevatorSpeed", getElevatorSpeed());
    // SmartDashboard.putNumber("Hanger/ElevatorPositionInInches", getHangerHeightInInches());
    // SmartDashboard.putNumber("Hanger/WinchSpeed", getWinchSpeed());
    // SmartDashboard.putNumber("Hanger/WinchPositionInInches", getWinchHeightInInches());
    // SmartDashboard.putNumber("Hanger/Elevator Motor Voltage", mElevatorMotor.getBusVoltage());
    SmartDashboard.putNumber("Hanger/kP", kP);
    SmartDashboard.putNumber("Hanger/kI", kI);
    SmartDashboard.putNumber("Hanger/kD", kD);
    SmartDashboard.putNumber("Hanger/kFF", kFF);
  }

  public void updatePIDs() {
    if (SmartDashboard.getNumber("Hanger/kP", 0.0) != mElevatorPIDController.getP()) {
      mElevatorPIDController.setP(SmartDashboard.getNumber("Hanger/kP", 1/18));
    }

    if (SmartDashboard.getNumber("Hanger/kI", 0.0) != mElevatorPIDController.getI()) {
      mElevatorPIDController.setI(SmartDashboard.getNumber("Hanger/kI", 0.0));
    }

    if (SmartDashboard.getNumber("Hanger/kD", 0.0) != mElevatorPIDController.getD()) {
      mElevatorPIDController.setD(SmartDashboard.getNumber("Hanger/kD", 0.0));
    }

    if (SmartDashboard.getNumber("Hanger/kFF", 0.0) != mElevatorPIDController.getFF()) {
      mElevatorPIDController.setFF(SmartDashboard.getNumber("Hanger/kFF", 0.0));
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run\
    logToDashboard();
    updatePIDs();
  }
}