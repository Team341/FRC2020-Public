/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.FloorIntake;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.FloorIntake;;

public class RunFloorIntakeByJoystick extends CommandBase {
  /**
   * Creates a new RunFloorIntakeByJoystick.
   */
  private FloorIntake mFloorIntake;
  private DoubleSupplier mRightY;
  
  // private boolean mIntakeState;
  // private boolean mLoweredIntake;

  public RunFloorIntakeByJoystick(FloorIntake floorIntake, DoubleSupplier rightY) {
    // Use addRequirements() here to declare subsystem dependencies.
    mRightY = rightY;
    mFloorIntake = floorIntake;

    addRequirements(mFloorIntake);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // mIntakeState = mFloorIntake.getFloorIntakeState();
    // if (!mIntakeState) {
    //   mLoweredIntake = true;
    //   mFloorIntake.setFloorIntakeState(true);
    // } else {
    //   mLoweredIntake = false;
    // }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    mFloorIntake.setIntakeSpeed(mRightY.getAsDouble());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mFloorIntake.setIntakeSpeed(0.0);
    // if (mLoweredIntake) {
    //   mFloorIntake.setFloorIntakeState(true);
    // }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
