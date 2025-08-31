package OldTeamCode.Autonomous;//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import HelpfulFunctions.Dijkstra.*;

import java.util.List;


/*
Some important notes:
- The claw servo starts at 0 here and 1 on the tournament opMode for some reason
 */
//@Config
@Autonomous(name = "Close to Bucket V1.2.143")
public class CloseToBucketAuton extends LinearOpMode {
    //region: Creating Variables
    //these variables correspond to servos and motors. They are displayed in order of distance to Control Hub.
    private DcMotor WheelMotorLeftFront;
    private DcMotor WheelMotorLeftBack;
    private DcMotor WheelMotorRightBack;
    private DcMotor WheelMotorRightFront;
    private DcMotor chainLeft;
    private DcMotor chainRight;
    private DcMotor extendoLeft;
    private DcMotor extendoRight;
    private DcMotor[] WheelMotors;
    private float ticksPerRevolution;
    private float wheelCircumference;
    private float wheelSideLength;
    private float dist;
    private float rot;
    private int task;
    private int target;
    private Field gameField;
    private int taskNumber;
    private boolean runOnce;
    private DcMotor armPivot;
    private Servo clawServo;
    private Servo otherClawServo;
    IntegratingGyroscope gyro;
    NavxMicroNavigationSensor navxMicro;
    //endregion

    @Override
    public void runOpMode() throws InterruptedException {
        //region: Initializing Variables
        //These variables do NOT correspond to a physical object; they are entirely digital and for coding purposes.
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        float speedMultipler = 0.4f;
        ticksPerRevolution = 537.7f;
        wheelCircumference = 11.8737f;
        wheelSideLength = 1.486f;
        dist = -36f;
        rot = 180f;
        task = 0;
        target = Integer.MAX_VALUE;
        gameField = new Field("3-1");
        taskNumber = 0;
        runOnce = true;
        clawServo = hardwareMap.servo.get("clawServo");
        clawServo.setPosition(1);
        otherClawServo = hardwareMap.servo.get("otherClawServo");
        otherClawServo.setPosition(0);


        //This section maps the variables to their corresponding motors/servos
        WheelMotorLeftFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftFront", hardwareMap);
        WheelMotorRightFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightFront", hardwareMap);
        WheelMotorLeftBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftBack", hardwareMap);
        WheelMotorRightBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightBack", hardwareMap);
        WheelMotorRightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        WheelMotorRightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        //This section creates an array of motors that will make some later code easier
        WheelMotors = new DcMotor[4];
        WheelMotors[0] = WheelMotorLeftFront;
        WheelMotors[1] = WheelMotorRightFront;
        WheelMotors[2] = WheelMotorLeftBack;
        WheelMotors[3] = WheelMotorRightBack;
        WheelMotors[0].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[2].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[3].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[0].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[1].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[2].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[3].setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        //This section initializes the motors that control the extension arms and sets their settings
        extendoLeft = hardwareMap.dcMotor.get("extendoLeft");
        extendoRight = hardwareMap.dcMotor.get("extendoRight");
        extendoRight.setDirection(DcMotorSimple.Direction.REVERSE);
        extendoLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendoRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extendoLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extendoRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armPivot = hardwareMap.dcMotor.get("armPivot");
        armPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        armPivot.setDirection(DcMotorSimple.Direction.REVERSE);
        //endregion

        ElapsedTime timer = new ElapsedTime();
        navxMicro = hardwareMap.get(NavxMicroNavigationSensor.class, "navx");
        gyro = (IntegratingGyroscope)navxMicro;

        // The gyro automatically starts calibrating. This takes a few seconds.
        telemetry.log().add("Gyro Calibrating. Do Not Move!");

        // Wait until the gyro calibration is complete
        timer.reset();
        while (navxMicro.isCalibrating())  {
            telemetry.addData("calibrating", "%s", Math.round(timer.seconds())%2==0 ? "|.." : "..|");
            telemetry.update();
            Thread.sleep(50);
        }
        telemetry.log().clear(); telemetry.log().add("Gyro Calibrated. Press Start.");
        telemetry.clear(); telemetry.update();
        float bigForward = 11.5f;

        //Wait for the user to press start
        waitForStart();
        //called continuously while OpMode is active

        while(opModeIsActive()) {
            /*
            Measurements:
            Circumference = 3.780"
            float ticksPerRevolution = ((((1+(46/17))) * (1+(46/11))) * 28);
            ticksPerRevolution = 537.7
             */

            //1.
            resetTask();
            List<List<String>> steps = gameField.getInstructionsList("6-1", "6-2");
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    List<String> taskList;
                    try {
                        taskList = steps.get(taskNumber);
                    }
                    catch(Exception e) {
                        break;
                    }
                    moveDirectionInInches(taskList.get(0), 24);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }

            //2.ROTATE 45 degrees clockwise
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    rotateToAngle(45);
                }

                taskNumber += 1;
                break;
            }

            //3. Move forward a teensie weensie bit
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("forward", bigForward);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getCurrentPosition() == WheelMotorLeftFront.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("right", 3f);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getCurrentPosition() == WheelMotorLeftFront.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //4. RAISE ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
//                    extendoLeft.setTargetPosition(3000);
//                    extendoRight.setTargetPosition(3000);
                    extendoLeft.setTargetPosition(3000);
                    extendoRight.setTargetPosition(3000);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("currpos:", extendoLeft.getCurrentPosition());
                telemetry.addData("otherpos:", extendoRight.getCurrentPosition());
                telemetry.update();
                if(extendoLeft.getTargetPosition() < -extendoLeft.getCurrentPosition()) {
                    extendoLeft.setPower(0);
                    extendoRight.setPower(0);
                    taskNumber += 1;
                    break;
                }
            }

            //5. PIVOT ARM DOWN
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(-160);
                    armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.75);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("Power", armPivot.getPower());
                telemetry.addData("RunModed", armPivot.getMode());
                telemetry.addData("Position:", armPivot.getCurrentPosition());
                telemetry.addData("Target: ", armPivot.getTargetPosition());
                telemetry.update();

                if(armPivot.getCurrentPosition() <= armPivot.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //6. RELEASE THINGY
            resetTask();
            while(true) {
                clawServo.setPosition(clawServo.getPosition() - 0.1);
                otherClawServo.setPosition(otherClawServo.getPosition() + 0.1);
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("clawServoPos:", clawServo.getPosition());
                telemetry.update();
                Thread.sleep(100);
                if(clawServo.getPosition() <= 0.6 && otherClawServo.getPosition() >= 0.5) {
                    taskNumber += 1;
                    break;
                }

            }
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    rotateToAngle(44);
                }

                taskNumber += 1;
                break;
            }
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    rotateToAngle(45);
                }

                taskNumber += 1;
                break;
            }
            Thread.sleep(1000);

            resetTask();
            while(true) {
                clawServo.setPosition(clawServo.getPosition() + 0.05);
                otherClawServo.setPosition(otherClawServo.getPosition() - 0.05);
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("clawServoPos:", clawServo.getPosition());
                telemetry.update();
                Thread.sleep(50);
                if(clawServo.getPosition() >= 1 && otherClawServo.getPosition() <= 0) {
                    taskNumber += 1;
                    break;
                }

            }

            //6.5. RAISE ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
//                    extendoLeft.setTargetPosition(3000);
//                    extendoRight.setTargetPosition(3000);
                    extendoLeft.setTargetPosition(2950);
                    extendoRight.setTargetPosition(2950);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("currpos:", extendoLeft.getCurrentPosition());
                telemetry.addData("otherpos:", extendoRight.getCurrentPosition());
                telemetry.update();
                if(extendoLeft.getTargetPosition() < -extendoLeft.getCurrentPosition()) {
                    extendoLeft.setPower(0);
                    extendoRight.setPower(0);
                    taskNumber += 1;
                    break;
                }
            }

            //7. PIVOT ARM UP
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(0);
                    armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.75);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("pos:", armPivot.getCurrentPosition());
                telemetry.update();

                if(armPivot.getCurrentPosition() >= armPivot.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            resetTask();
            while(true) {
                clawServo.setPosition(clawServo.getPosition() - 0.1);
                otherClawServo.setPosition(otherClawServo.getPosition() + 0.1);
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("clawServoPos:", clawServo.getPosition());
                telemetry.update();
                Thread.sleep(100);
                if(clawServo.getPosition() <= 0.6 && otherClawServo.getPosition() >= 0.5) {
                    taskNumber += 1;
                    break;
                }

            }

            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("back", 0.5f);

                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getTargetPosition() == WheelMotorLeftFront.getCurrentPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //8. LOWER ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    extendoLeft.setDirection(DcMotorSimple.Direction.REVERSE);
                    extendoRight.setDirection(DcMotorSimple.Direction.FORWARD);

                    extendoLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    extendoRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                    extendoLeft.setTargetPosition(2800);
                    extendoRight.setTargetPosition(2800);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("currpos:", extendoLeft.getCurrentPosition());
                telemetry.update();

                if(extendoLeft.getTargetPosition() < -extendoLeft.getCurrentPosition()) {
                    extendoLeft.setPower(0);
                    extendoRight.setPower(0);
                    taskNumber += 1;
                    break;
                }
            }

            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("forward", 1f);

                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getTargetPosition() == WheelMotorLeftFront.getCurrentPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("left", 2.5f);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getCurrentPosition() == WheelMotorLeftFront.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }
            //9. Move backwards 12 inches
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("back", bigForward);

                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getTargetPosition() == WheelMotorLeftFront.getCurrentPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //10. Rotate to standard rotation
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    rotateToAngle(0);
                }

                taskNumber += 1;
                break;
            }

            //11. Move 12.5 inches right
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("right", 14f);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getCurrentPosition() == WheelMotorLeftFront.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //11.5 MOVE .5 INCH FORWARD
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("forward", 0.5f);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("Current position", WheelMotorLeftFront.getCurrentPosition());
                telemetry.addData("Target position", WheelMotorLeftFront.getTargetPosition());
                telemetry.update();

                if((Math.abs(WheelMotorLeftFront.getCurrentPosition()) < Math.abs(WheelMotorLeftFront.getTargetPosition()) + 1 || Math.abs(WheelMotorLeftFront.getCurrentPosition()) > Math.abs(WheelMotorLeftFront.getTargetPosition()) - 1)) {
                    taskNumber += 1;
                    break;
                }
            }

            //12. PIVOT ARM DOWN
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(-230);
                    armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.5);
                }
//                if(armPivot.getCurrentPosition() < -200) {
//                    armPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//                    armPivot.setPower(0.075);
//                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("Power", armPivot.getPower());
                telemetry.addData("RunModed", armPivot.getMode());
                telemetry.addData("Position:", armPivot.getCurrentPosition());
                telemetry.addData("Target: ", armPivot.getTargetPosition());
                telemetry.update();

                if(armPivot.getCurrentPosition() < armPivot.getTargetPosition()) {
                    armPivot.setPower(0);
                    taskNumber += 1;
                    break;
                }
            }
            Thread.sleep(1000);

            //13. GRAB NEW BLOCK
            resetTask();
            while(true) {
                clawServo.setPosition(clawServo.getPosition() + 0.05);
                otherClawServo.setPosition(otherClawServo.getPosition() - 0.05);
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("clawServoPos:", clawServo.getPosition());
                telemetry.update();
                Thread.sleep(50);
                if(clawServo.getPosition() >= 1 && otherClawServo.getPosition() <= 0) {
                    taskNumber += 1;
                    break;
                }

            }

            //14. PIVOT ARM UP
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(0);
                    armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.75);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("Power", armPivot.getPower());
                telemetry.addData("RunModed", armPivot.getMode());
                telemetry.addData("Position:", armPivot.getCurrentPosition());
                telemetry.addData("Target: ", armPivot.getTargetPosition());
                telemetry.update();

                if(armPivot.getCurrentPosition() >= -40) {
                    armPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    armPivot.setPower(-0.01);
                    taskNumber += 1;
                    break;
                }
            }

            //14.5 MOVE .5 INCHES BACK
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("back", 0.5f);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if((Math.abs(WheelMotorLeftFront.getCurrentPosition()) < Math.abs(WheelMotorLeftFront.getTargetPosition()) + 1 || Math.abs(WheelMotorLeftFront.getCurrentPosition()) > Math.abs(WheelMotorLeftFront.getTargetPosition()) - 1)) {
                    taskNumber += 1;
                    break;
                }
            }


            //15 MOVE 12 INCHES LEFT
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("left", 14);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if(WheelMotorLeftFront.getTargetPosition() == WheelMotorLeftFront.getCurrentPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //16.ROTATE 45 degrees clockwise
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    rotateToAngle(45);
                }

                taskNumber += 1;
                break;
            }

            //16.5 MOVE FORWARD 12 INCHES
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    moveDirectionInInches("forward", bigForward);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.update();

                if((Math.abs(WheelMotorLeftFront.getCurrentPosition()) < Math.abs(WheelMotorLeftFront.getTargetPosition()) + 2 || Math.abs(WheelMotorLeftFront.getCurrentPosition()) > Math.abs(WheelMotorLeftFront.getTargetPosition()) - 2)) {
                    taskNumber += 1;
                    break;
                }
            }

            //17. RAISE ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    extendoLeft.setDirection(DcMotorSimple.Direction.FORWARD);
                    extendoRight.setDirection(DcMotorSimple.Direction.REVERSE);

                    extendoLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    extendoRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                    extendoLeft.setTargetPosition(2800);
                    extendoRight.setTargetPosition(2800);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                }

                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("currpos:", extendoLeft.getCurrentPosition());
                telemetry.update();

                if(extendoLeft.getTargetPosition() < -extendoLeft.getCurrentPosition()) {
                    extendoLeft.setPower(0);
                    extendoRight.setPower(0);
                    taskNumber += 1;
                    break;
                }
            }

            //18. PIVOT ARM DOWN
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(-150);
                        armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.5);
                }
                if(armPivot.getCurrentPosition() < -120) {
                    armPivot.setPower(0.1);
                }
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("Power", armPivot.getPower());
                telemetry.addData("RunModed", armPivot.getMode());
                telemetry.addData("Position:", armPivot.getCurrentPosition());
                telemetry.addData("Target: ", armPivot.getTargetPosition());
                telemetry.update();

                if(armPivot.getCurrentPosition() < armPivot.getTargetPosition()) {
                    taskNumber += 1;
                    break;
                }
            }

            //19. RELEASE THINGY
            resetTask();
            while(true) {
                clawServo.setPosition(clawServo.getPosition() - 0.1);
                otherClawServo.setPosition(otherClawServo.getPosition() + 0.1);
                telemetry.addData("Task number:", taskNumber);
                telemetry.addData("clawServoPos:", clawServo.getPosition());
                telemetry.update();
                Thread.sleep(100);
                if(clawServo.getPosition() <= 0.6 && otherClawServo.getPosition() >= 0.5) {
                    taskNumber += 1;
                    break;
                }

            }
            stop();
            break;
            /*
            PSUEDOSTEPS:
            Anything indented has been implemented
             1. Move forward to 6-2
             2. Rotate left 45 clockwise 45 degrees
             3. Move forward 14 inches more
             4. Raise the arms (to pos 3000)
             5. Pivot the arm down
             6. Release the block
             7. Pivot the arm up
             8. Lower the arms
             9. Move backwards 14 inches
             10. Rotate counterclockwise 45 degrees
             11. Move right 13.8 inches
             11.5 Move back 5 inches
             12. Pivot arm down
             13. Grab new block
             14. Pivot the arm up
            15. Move left 13.8 inches
            16. Rotate clockwise 45 degrees
            16. Move forward 9 inches
            17. Raise the arms (to pos 3050)
            18. Pivot the arm down
            19. Release the block
             */
        }
    }

    public void resetTask() {
        target = Integer.MAX_VALUE;
        runOnce = true;
    }
    public void moveDirectionInInches(String direction, float distance) {
        if(direction == "right") {
            moveDistanceInInchesRight(distance);
        }
        if(direction == "left") {
            moveDistanceInInchesRight(-distance);
        }
        if(direction == "forward") {
            moveDistanceInInches(distance);
        }
        if(direction == "back") {
            moveDistanceInInches(-distance);
        }
    }

    public void rotateToAngle(float degrees) {
        for(DcMotor motor : WheelMotors) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        Orientation angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double power = 0.25;
        if(degrees < angles.firstAngle) {
            power = -power;
        }
        while(Math.round(AngleUnit.DEGREES.normalize(angles.firstAngle)) > degrees + 1 || Math.round(AngleUnit.DEGREES.normalize(angles.firstAngle)) < degrees - 1) {
            telemetry.addData("navx pos Z", AngleUnit.DEGREES.normalize(angles.firstAngle));
            telemetry.addData("navx pos Y", AngleUnit.DEGREES.normalize(angles.secondAngle));
            telemetry.addData("navx pos X", AngleUnit.DEGREES.normalize(angles.thirdAngle));


            WheelMotorLeftFront.setPower(power);
            WheelMotorLeftBack.setPower(power);
            WheelMotorRightBack.setPower(-power);
            WheelMotorRightFront.setPower(-power);
            angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            telemetry.addData("power", power);
            telemetry.update();
        }
        WheelMotorLeftFront.setPower(0);
        WheelMotorLeftBack.setPower(0);
        WheelMotorRightBack.setPower(0);
        WheelMotorRightFront.setPower(0);

    }

    //    public void rotateInDegrees(float degrees)
//    {
//        /*
//        Rotates in place n degrees clockwise. Use negative values for counterclockwise.
//         */
//        int magnitude = (int) Math.round(4600 * Math.sin((Math.PI/180) * degrees/6.325));
//        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorLeftFront.setTargetPosition(magnitude);
//        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        target = magnitude;
//
//        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorLeftBack.setTargetPosition(magnitude);
//        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorRightFront.setTargetPosition(-magnitude);
//        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        WheelMotorRightBack.setTargetPosition(-magnitude);
//        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        for(DcMotor motor : WheelMotors) {
//            motor.setPower(0.25);
//        }
//    }
    public void moveDistanceInInchesRight(float distance) {
        /*
        Moves a float distance of inches right. Use negative values for left.
         */

        int magnitude = Math.round((distance/wheelSideLength) * (ticksPerRevolution * (wheelSideLength/wheelCircumference)));
        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftFront.setTargetPosition(magnitude);
        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        target = magnitude;

        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftBack.setTargetPosition(-magnitude);
        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightFront.setTargetPosition(-magnitude);
        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightBack.setTargetPosition(magnitude);
        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        for(DcMotor motor : WheelMotors) {
            motor.setPower(0.25);
        }
    }
    public void moveDistanceInInches(float distance) {
        /*
        Moves distance inches forward. Use negative values for backward.
         */
        for(DcMotor motor : WheelMotors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(-Math.round((distance/wheelCircumference) * ticksPerRevolution));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        target = -Math.round((distance/wheelCircumference) * ticksPerRevolution);
        for(DcMotor motor: WheelMotors) {
            motor.setPower(0.5);
        }
    }

    public void moveRobotInDirection(String direction, float speedMultipler) {
        if (direction == "forward") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "backward") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        } else if (direction == "left") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "right") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        }
    }

    public void stopRobot() {
        WheelMotorLeftFront.setPower(0);
        WheelMotorRightFront.setPower(0);
        WheelMotorLeftBack.setPower(0);
        WheelMotorRightBack.setPower(0);
    }
}