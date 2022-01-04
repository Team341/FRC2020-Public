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

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Sorter extends SubsystemBase {
  /**
   * Creates a new Sorter.
   */

  private static Sorter instance = null;
  public static Sorter getInstance() {
    if(instance == null) {
      instance = new Sorter();
    }
    return instance;
  }

  // creates a new TalonSRX motor for the sorter
  private TalonSRX mSorterMotor;

  public Sorter() {
    // Setup the sorter motor (1x 775Pro)
    mSorterMotor = new TalonSRX(Constants.Sorter.SORTER_MOTOR_PORT);
    
    // Configure the sorter motor
    mSorterMotor.configPeakCurrentLimit(Constants.Sorter.SORTER_CURRENT_LIMIT, Constants.TIMEOUT_MS);
    mSorterMotor.enableCurrentLimit(true);
    mSorterMotor.setNeutralMode(NeutralMode.Brake);
    mSorterMotor.setInverted(false);
  }

  /**
   * runs the sorter motor at speed defined in constants
   */
  public void run() {
    mSorterMotor.set(ControlMode.PercentOutput, Constants.Sorter.SORTER_FORWARDS_SPEED);
  }

  /**
   * runs the sorter motor backwards at speed defined in constants
   */
  public void runBackwards() {
    mSorterMotor.set(ControlMode.PercentOutput, Constants.Sorter.SORTER_BACKWARDS_SPEED);
  }

  /**
   * sets the speed of the sorter motor
   * @param speed double [-1, 1]
   */
  public void setSpeed(double speed) {
    mSorterMotor.set(ControlMode.PercentOutput, speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
