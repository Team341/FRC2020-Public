/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.MultiSubsystemCommandGroups;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.Shooter.ShooterRPM;
import frc.robot.commands.Tower.RunTower;
import frc.robot.commands.Tower.RunTowerTrigger;
import frc.robot.commands.Vision.AlignToGoal;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Tower;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class Shoot extends ParallelCommandGroup {
  /**
   * Creates a new Shoot.
   */
  private Shooter mShooter;
  private Drive mDrive;
  private Tower mTower;
  private DoubleSupplier mLeftY, mRightX;
  private boolean mTrigLeft, mTrigRight;
  public Shoot(Drive drive, Shooter shooter, Tower tower, DoubleSupplier leftY, DoubleSupplier rightX, boolean trigLeft, boolean trigRight) {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());super();

    mDrive = drive;
    mShooter = shooter;
    mTower = tower;
    mLeftY = leftY;
    mRightX = rightX;
    mTrigLeft = trigLeft;
    mTrigRight = trigRight;

    addCommands(
      new AlignToGoal(mDrive, mLeftY, mRightX, () -> mTrigLeft),

      new ShooterRPM(mShooter, mTower, () -> mTrigLeft, false),

      new RunTowerTrigger(mTower, mTrigRight)
    );
  }
}
