/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Tower;

import java.util.Arrays;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Sorter;
import frc.robot.subsystems.Tower;

public class EmptyTower extends CommandBase {
  /**
   * Creates a new RunTowerTrigger.
   */

  private Tower mTower;
  private Shooter mShooter;
  private Sorter mSorter;
  private boolean mIsShooting;
  private Integer emptyCount;
  private int countSinceShot;
  public EmptyTower(Tower tower, Shooter shooter, Sorter sorter) {
    // Use addRequirements() here to declare subsystem dependencies.
    mTower = tower;
    mShooter = shooter;
    mSorter = sorter;
    mIsShooting = false;
    addRequirements(mTower, mSorter);
    emptyCount = 0;
    countSinceShot = 0;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (mTower.getBeamBreakOneState() == false) {
      mTower.run();
    } else if (mShooter.getOnTargetCount() >= 5.0) {// && mTower.getBeamBreakOneState()) {
      mTower.run();
      mIsShooting = true;
    } else {
      mTower.setTowerSpeed(0.0);
      mIsShooting = false;
    }
    emptyCount = 0;
    countSinceShot = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (mTower.getBeamBreakOneState()) {
      countSinceShot = 0;
    } else {
      countSinceShot++;
    }
    if (mIsShooting && countSinceShot > 3){//} && mTower.getBeamBreakOneState() == false) {
      mIsShooting = false;
    }
    if (mTower.getBeamBreakOneState() == false || mIsShooting) {
      mTower.run();
      mSorter.run();
    } else if (mShooter.getOnTargetCount() >= 5.0 ) {// && mTower.getBeamBreakOneState()) {
      mTower.run();
      mSorter.run();
      mIsShooting = true;
    } else {
      mSorter.setSpeed(0.0);
      mTower.setTowerSpeed(0.0);
    }
    if (!mTower.getBeamBreakOneState() && !mTower.getBeamBreakTwoState() && !mTower.getBeamBreakThreeState() && !mTower.getBeamBreakFourState()) {
      emptyCount += 1;
    } else {
      emptyCount = 0;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mTower.setTowerSpeed(0.0);
    mSorter.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // We are done when no beam breaks are triggered
    return emptyCount >= 7;
  }
}
