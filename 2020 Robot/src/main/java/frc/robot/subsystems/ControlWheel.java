package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

public class ControlWheel extends SubsystemBase {
  /**
   * Creates a new ControlWheel
   */

  //Returns an instance of the ControlWheel
  private static ControlWheel instance = null; 
  public static ControlWheel getInstance() {
    if(instance == null) {
      instance = new ControlWheel();
    }
    return instance;
  }

  private final ColorMatch mColorMatcher;
  private final I2C.Port mI2CPort;
  private final ColorSensorV3 mColorSensor;

  // primitive variables for logic
  private Color mLastColor;
  private int mIterationCounter;

  // creates motor and solenoid refernces
  private CANSparkMax mControlWheelMotor;
  private Solenoid mControlWheelSolenoid;
  
  // creates digital input references
  private DigitalInput mLimitSwitchLeft;
  private DigitalInput mLimitSwitchRight;

  public ControlWheel() {
    // instanciates color matcher and sensor
    mColorMatcher = new ColorMatch();
    mColorMatcher.setConfidenceThreshold(.99);
    mI2CPort = I2C.Port.kOnboard;
    mColorSensor = new ColorSensorV3(mI2CPort);

    // adds colors to matcher
    mColorMatcher.addColorMatch(Constants.ControlWheel.BLUE_TARGET);
    mColorMatcher.addColorMatch(Constants.ControlWheel.GREEN_TARGET);
    mColorMatcher.addColorMatch(Constants.ControlWheel.RED_TARGET);
    mColorMatcher.addColorMatch(Constants.ControlWheel.YELLOW_TARGET);

    // instanciates primitive types for logic
    mLastColor = null;
    mIterationCounter = 0;

    // instanciates motor to instance of CANSparkMax class
    mControlWheelMotor = new CANSparkMax(Constants.ControlWheel.CONTROL_WHEEL_MOTOR, MotorType.kBrushless);
    mControlWheelMotor.setIdleMode(IdleMode.kBrake);

    mControlWheelSolenoid = new Solenoid(Constants.ControlWheel.CONTROL_WHEEL_SOLENOID_PORT);

    // Set up the limit switches on the color wheel system
    mLimitSwitchLeft = new DigitalInput(Constants.ControlWheel.LEFT_LIMIT_SWITCH_PORT);
    mLimitSwitchRight = new DigitalInput(Constants.ControlWheel.RIGH_LIMIT_SWITCH_PORT);
  }

  /**
   * setable threshold of the color matcher
   * @param double threshold
   */
  public void setConfidenceThreshold(double threshold) {
    mColorMatcher.setConfidenceThreshold(threshold);
  }

  /**
   * sets the speed of the motor for spinning the command panel
   * @param speed
   */
  public void setSpeed(double speed) {
    mControlWheelMotor.set(speed);
  }

  /**
   * setable state of the control wheel solenoid
   * @param boolean state
   */
  public void setControlWheelSolenoidState(boolean state) {
    mControlWheelSolenoid.set(state);
  }

  /**
   * settable integer for color last detected
   * @param Color last color detected
   */
  public void setLastColor(Color lastColor){
    mLastColor = lastColor;
  }

  /**
   * set that an iteration has occured
   */
  public void setIterate(){
    mIterationCounter++;
  }

  /**
   * settable iteration value
   * @param int interation count
   */
  public void setIterateValue(int iteration){
    mIterationCounter = iteration;
  }

  /**
   * Returns the last color detected by the color sensor
   * @return mLastColor 0 = green, 1 = blue, 2 = yellow, 3 = red
   */
  public Color getLastColor(){
    return mLastColor;
  }

  public Color getColor(){
    return mColorMatcher.matchClosestColor(mColorSensor.getColor()).color;
  }

  /**
   * Returns the integer value of the color detected
   * @return mLastColor 0 = green, 1 = blue, 2 = yellow, 3 = red
   */
  public int getColorInt(){
    ColorMatchResult mMatch = mColorMatcher.matchClosestColor(mColorSensor.getColor());
    
    if (mMatch.color == Constants.ControlWheel.RED_TARGET){
      return 3;
    } else if (mMatch.color == Constants.ControlWheel.YELLOW_TARGET){
      return 2;
    } else if (mMatch.color == Constants.ControlWheel.BLUE_TARGET){
      return 1;
    } else if (mMatch.color == Constants.ControlWheel.GREEN_TARGET){
      return 0;
    } else {
      return -1;
    }
  }

  /**
   * @param int color integer 0 = green, 1 = blue, 2 = yellow, 3 = red
   * @return color corresponding to that integer
   */
  public Color getColorFromInt(int color){
    if (color == 3){
      return Constants.ControlWheel.RED_TARGET;
    } else if (color == 2){
      return Constants.ControlWheel.YELLOW_TARGET;
    } else if (color == 1){
      return Constants.ControlWheel.BLUE_TARGET;
    } else if (color == 0){
      return Constants.ControlWheel.GREEN_TARGET;
    } else {
      return null;
    }
  }

  /**
   * Gets closest detected color
   * @return Color Match Result
   */
  public ColorMatchResult getClosestColor() {
    return mColorMatcher.matchClosestColor(mColorSensor.getColor());
  }

  /**
   * Gets the state of left limit switch
   * @return left limit switch state
   */
  public boolean getLimitSwitchLeft() {
    return !mLimitSwitchLeft.get();
  }

  /**
   * Gets the state of the right limit switch
   * @return right limit switch state
   */
  public boolean getLimitSwitchRight() {
    return !mLimitSwitchRight.get();
  }

  /**
   * get motor position
   * @return double motor position from encoder
   */
  public double getMotorEncoderPos() {
    return mControlWheelMotor.getEncoder().getPosition();
  }

  /**
   * Gets if the control wheel is lowed; true = lowered
   * @return Boolean is the control wheel manipulator down
   */
  public boolean getControlWheelSolenoidState() {
    return mControlWheelSolenoid.get();
  }

  /**
   * get integer desired color to turn to
   * @return int color, 0 = green, 1 = blue, 2 = yellow, 3 = red
   */
  public int getSmartDashboardColor() {
    String color = DriverStation.getInstance().getGameSpecificMessage();
    int colorVal = 99;
    if (color.length() > 0.0) {
      switch (color.charAt(0)) {
        case 'B':
          colorVal = 1;
          break;
        case 'G':
          colorVal = 0;
          break;
        case 'R':
          colorVal = 3;
          break;
        case 'Y':
          colorVal = 2;
          break;
        default:
          colorVal = 99;
          break;
      }
    }
    if (colorVal != 99) {
      colorVal = (colorVal + 2) % 4;
    }
    SmartDashboard.putNumber("ColorWheel/color val", colorVal);
    SmartDashboard.putString("ColorWheel/Color", color);
    return colorVal;
  }

  public void logToDashboard() {
    // SmartDashboard.putNumber("Detected Color Int", getColorInt);
    SmartDashboard.putNumber("Control Wheel/Iteration", mIterationCounter);
    SmartDashboard.putBoolean("Control Wheel/Limit Switch Left", getLimitSwitchLeft());
    SmartDashboard.putBoolean("Control Wheel/Limit Switch Right", getLimitSwitchRight());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // logToDashboard();
  }
}