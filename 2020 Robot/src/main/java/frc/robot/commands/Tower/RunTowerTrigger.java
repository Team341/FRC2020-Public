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
import frc.robot.subsystems.Tower;

public class RunTowerTrigger extends CommandBase {
  /**
   * Creates a new RunTowerTrigger.
   */

  private Tower mTower;
  private boolean[] mStateArray;
  private boolean mTrigRight;

  private boolean[] mffff = {false, false, false, false};
  private boolean[] mffft = {false, false, false, true };
  private boolean[] mfftf = {false, false, true , false};
  private boolean[] mfftt = {false, false, true , true };
  private boolean[] mftff = {false, true , false, false};
  private boolean[] mftft = {false, true , false, true };
  private boolean[] mfttf = {false, true , true , false};
  private boolean[] mfttt = {false, true , true , true };
  private boolean[] mtfff = {true , false, false, false};
  private boolean[] mtfft = {true , false, false, true };
  private boolean[] mtftf = {true , false, true , false};
  private boolean[] mtftt = {true , false, true , true };
  private boolean[] mttff = {true , true , false, false};
  private boolean[] mttft = {true , true , false, true };
  private boolean[] mtttf = {true , true , true , false};
  private boolean[] mtttt = {true , true , true , true };

  public RunTowerTrigger(Tower tower, boolean trigRight) {
    // Use addRequirements() here to declare subsystem dependencies.
    mTower = tower;
    mTrigRight = trigRight;

    mStateArray = new boolean[4];
    mStateArray[0] = mTower.getBeamBreakFourState();
    mStateArray[1] = mTower.getBeamBreakThreeState();
    mStateArray[2] = mTower.getBeamBreakTwoState();
    mStateArray[3] = mTower.getBeamBreakOneState();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (mTrigRight) {
      if (Arrays.equals(mStateArray, mffff) || Arrays.equals(mStateArray, mffft) || Arrays.equals(mStateArray, mfftt) || Arrays.equals(mStateArray, mfttt)){
        mTower.run();;
      } else if (Arrays.equals(mStateArray, mfftf) || Arrays.equals(mStateArray, mfttf) || Arrays.equals(mStateArray, mtttf) || Arrays.equals(mStateArray, mtttt)){
        mTower.setTowerSpeed(0.0);
      } else if (Arrays.equals(mStateArray, mftff) || Arrays.equals(mStateArray, mtfff) || Arrays.equals(mStateArray, mttff)){
        mTower.runBackwards();;
      } else if (Arrays.equals(mStateArray, mftft)){
        mTower.run();
        SmartDashboard.putString("Tower/Jam Error", "possible jam or spacing issue");
      } else if (Arrays.equals(mStateArray, mtfft) || Arrays.equals(mStateArray, mtftf) || Arrays.equals(mStateArray, mtftt) || Arrays.equals(mStateArray, mttft)){
        mTower.setTowerSpeed(0.0);
        SmartDashboard.putString("Tower/Jam Error", "possible jam or spacing issue");
      }
    } else {
      mTower.setTowerSpeed(0.0);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mTower.setTowerSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
