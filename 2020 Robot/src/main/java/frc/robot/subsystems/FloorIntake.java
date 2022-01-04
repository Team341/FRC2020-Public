/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class FloorIntake extends SubsystemBase {
  /**
   * creates a new FloorIntake
   */

  private static FloorIntake instance = null;
  public static FloorIntake getInstance() {
    if(instance == null) {
      instance = new FloorIntake();
    }
    return instance;
  }

  // creates a new motor object for intake wheels
  private final CANSparkMax mFloorIntakeMotor;

  // creates a new solenoid object
  private final Solenoid mSolenoid;
  
  public FloorIntake() {
    // Setup the floor intake motor motor (1x Neo550)
    mFloorIntakeMotor = new CANSparkMax(Constants.FloorIntake.FLOOR_INTAKE_MOTOR_PORT, MotorType.kBrushless);
    
    // Configure intake motor current and Idle mode
    mFloorIntakeMotor.restoreFactoryDefaults();
    mFloorIntakeMotor.setSmartCurrentLimit(Constants.FloorIntake.FLOOR_INTAKE_MOTOR_CURRENT_LIMIT);
    mFloorIntakeMotor.setIdleMode(IdleMode.kCoast);
    mFloorIntakeMotor.setInverted(true);

    // Burn configuration to flash memory to prevent issues if power is lost while robot is operating
    mFloorIntakeMotor.burnFlash();

    // instantiates solenoid object to new Solenoid class
    mSolenoid = new Solenoid(Constants.FloorIntake.SOLENOID_PORT);
  }

  /**
   * runs the intake at the speed defined in Constants
   */
  public void runIntake() {
    mFloorIntakeMotor.set(Constants.FloorIntake.RUN_SPEED);
  }

  /**
   * runs the intake at the reverse speed defined in Constants
   */
  public void reverseIntake() {
    mFloorIntakeMotor.set(Constants.FloorIntake.REVERSE_SPEED);
  }

  /**
   * sets the speed double of the intake 
   * @param speed
   */
  public void setIntakeSpeed(double speed) {
    mFloorIntakeMotor.set(speed);
  }

  /**
   * gets the voltage double of the motor
   * @return returns the motor's voltage
   */
  public double getMotorCommandVoltage() {
    return mFloorIntakeMotor.getBusVoltage();
  }

  /**
   * sets the boolean state of the floor intake
   * @param State
   */
  public void setFloorIntakeState(boolean state) {
    mSolenoid.set(state);
  }

  /**
   * gets the state of the floor intake if it's lowered or not
   * @return boolean true = down, false = up
   */
  public Boolean getFloorIntakeState() {
    return mSolenoid.get();
  }

  /**
   * logs the motor current and the boolean value of the floor intake to the dashboard
   */
  public void logToDashboard() {
    SmartDashboard.putBoolean("Floor Intake/Is Floor Intake Lowered", getFloorIntakeState());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // logToDashboard();
  }
}