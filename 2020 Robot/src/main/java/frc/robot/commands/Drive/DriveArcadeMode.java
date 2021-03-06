/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Drive;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drive;
import frc.robot.utilities.AlphaFilter;

public class DriveArcadeMode extends CommandBase {
  /**
   * Creates a new DriveArcadeMode.
   */  

  private RobotContainer mRobotContainer;
  private Drive mDrive;
  private DoubleSupplier mLeftY;
  private DoubleSupplier mRightX;
  // private AlphaFilter mAlphaFilter;
  private AlphaFilter mSpeedFilter;
  private AlphaFilter mTurnFilter;

  public DriveArcadeMode(Drive drive, DoubleSupplier leftY, DoubleSupplier rightX) {

    // Use addRequirements() here to declare subsystem dependencies.
    //mRobotContainer = RobotContainer.getInstance();
    // mAlphaFilter = new AlphaFilter(Constants.Drive.ALPHA_FILTER);
    mSpeedFilter = new AlphaFilter(Constants.Drive.ALPHA_FILTER);
    mTurnFilter = new AlphaFilter(Constants.Drive.ALPHA_FILTER);
    mDrive = drive;
    mLeftY = leftY;
    mRightX = rightX;
    addRequirements(mDrive);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    // mDrive.getDifferentialDrive().arcadeDrive(mAlphaFilter.calculate(mLeftY.getAsDouble()), mAlphaFilter.calculate(mRightX.getAsDouble() * Constants.Drive.TURN_DAMPENING_FACTOR));
    double speed = mLeftY.getAsDouble();
    double turn = mRightX.getAsDouble();

    // Cube the joystick values to deaden it near the zero, give more control through the middle section. See https://www.chiefdelphi.com/t/paper-joystick-sensitivity-gain-adjustment/107280
    speed *= speed * speed;
    turn *= turn * turn;

    // filter the speed to slowly ramp up
    // speed = mSpeedFilter.calculate(speed);

    // Issue the speed commands to the differential drive
    mDrive.getDifferentialDrive().arcadeDrive(speed, turn);
    // mDrive.getDifferentialDrive().arcadeDrive(mLeftY.getAsDouble(), mRightX.getAsDouble() * Constants.Drive.TURN_DAMPENING_FACTOR);

    SmartDashboard.putNumber("LeftStick Y", speed);
    // mDrive.getDifferentialDrive().arcadeDrive(mLeftY.getAsDouble(), mRightX.getAsDouble() * Constants.Drive.TURN_DAMPENING_FACTOR);

    SmartDashboard.putNumber("LeftStick Y", speed);
    // System.out.println("LeftStick Y: " + mRobotContainer.mDriveLeftStickY);
    SmartDashboard.putNumber("RigthStick X", turn);
    // System.out.println("RightStick X" + mRobotContainer.mDriveRightStickX);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
