/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Tower;

import java.lang.reflect.Array;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Tower;
import java.util.Arrays;

public class RunTower extends CommandBase {
  /**
   * Creates a new RunTower.
   */

  private Tower mTower;
  private boolean[] mStateArray;

  // // giant list of boolean arrays to compare against return from beam break
  // // explained at http://www.team341.com/mybbforum/showthread.php?tid=1159
  // // goes in descending order (first is the fourth beam break, fourth is the first)
  // private boolean[] mffff = {false, false, false, false};
  // private boolean[] mffft = {false, false, false, true };
  // private boolean[] mfftf = {false, false, true , false};
  // private boolean[] mfftt = {false, false, true , true };
  // private boolean[] mftff = {false, true , false, false};
  // private boolean[] mftft = {false, true , false, true };
  // private boolean[] mfttf = {false, true , true , false};
  // private boolean[] mfttt = {false, true , true , true };
  // private boolean[] mtfff = {true , false, false, false};
  // private boolean[] mtfft = {true , false, false, true };
  // private boolean[] mtftf = {true , false, true , false};
  // private boolean[] mtftt = {true , false, true , true };
  // private boolean[] mttff = {true , true , false, false};
  // private boolean[] mttft = {true , true , false, true };
  // private boolean[] mtttf = {true , true , true , false};
  // private boolean[] mtttt = {true , true , true , true };

  boolean movingBallUpASpot = false;
  int nextBallSlot = 2; 

  public RunTower(Tower tower) {
    // Use addRequirements() here to declare subsystem dependencies.

    mTower = tower;
    addRequirements(mTower);

    mStateArray = new boolean[4];
    mStateArray[3] = mTower.getBeamBreakFourState();
    mStateArray[2] = mTower.getBeamBreakThreeState();
    mStateArray[1] = mTower.getBeamBreakTwoState();
    mStateArray[0] = mTower.getBeamBreakOneState();
  }

  // Called when the command is initially scheduled.
  // everything is defined at http://www.team341.com/mybbforum/showthread.php?tid=1159
  @Override
  public void initialize() {
    movingBallUpASpot = false;
    nextBallSlot = 2; 
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // if (Arrays.equals(mStateArray, mffff) || Arrays.equals(mStateArray, mffft) || Arrays.equals(mStateArray, mfftt) || Arrays.equals(mStateArray, mfttt)){
    //   mTower.run();
    // } else if (Arrays.equals(mStateArray, mfftf) || Arrays.equals(mStateArray, mfttf) || Arrays.equals(mStateArray, mtttf) || Arrays.equals(mStateArray, mtttt)){
    //   mTower.setTowerSpeed(0.0);
    // } else if (Arrays.equals(mStateArray, mftff) || Arrays.equals(mStateArray, mtfff) || Arrays.equals(mStateArray, mttff)){
    //   mTower.runBackwards();
    // } else if (Arrays.equals(mStateArray, mftft)){
    //   mTower.run();
    //   SmartDashboard.putString("Tower/Jam Error", "possible jam or spacing issue");
    // } else if (Arrays.equals(mStateArray, mtfft) || Arrays.equals(mStateArray, mtftf) || Arrays.equals(mStateArray, mtftt) || Arrays.equals(mStateArray, mttft)){
    //   mTower.setTowerSpeed(0.0);
    //   SmartDashboard.putString("Tower/Jam Error", "possible jam or spacing issue");
    // }

    //Updating Beam Break Values
    mStateArray[3] = mTower.getBeamBreakFourState();
    mStateArray[2] = mTower.getBeamBreakThreeState();
    mStateArray[1] = mTower.getBeamBreakTwoState();
    mStateArray[0] = mTower.getBeamBreakOneState();

    if (movingBallUpASpot) {
      if (mStateArray[nextBallSlot]) {
        mTower.setTowerSpeed(0.0);
        movingBallUpASpot = false;
      } else {
        mTower.setTowerSpeed(Constants.Tower.TOWER_LOAD_SPEED);
      }
    } else {
      // Determine if we can run the conveyor
      if (mStateArray[2] || mStateArray[3]) {
        // We have a ball at the start of the conveyor, see if we can intake it...
        if (mStateArray[0] || mStateArray[1]) {
          // We already have a ball at the top of the tower, we can't load any more balls
          movingBallUpASpot = false;
        } else if (mStateArray[1]) {
          // Move the ball up to the top of the tower from the bottom of the tower
          nextBallSlot = 0;
          movingBallUpASpot = true;
        }  else {//} if (mStateArray[2]) {
            // Move the ball up from the conveyor to the bottom of the tower
            nextBallSlot = 1;
            movingBallUpASpot = true;
        // } else {
        //   // Move the ball from the start of the conveyor into the conveyor
        //   nextBallSlot = 2;
        //   movingBallUpASpot = true; 
        }
      }
    }

    // System.out.println("Moving Ball Up: " + movingBallUpASpot);
    // System.out.println("Next Ball SLot: " + nextBallSlot);
    SmartDashboard.putNumber("Tower/Next Ball Slot", nextBallSlot);
    SmartDashboard.putBoolean("Tower/Ball is Moving Up A SLot", movingBallUpASpot);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    mTower.setTowerSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return mStateArray[0];
  }
}
