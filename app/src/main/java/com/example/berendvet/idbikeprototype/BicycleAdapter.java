package com.example.berendvet.idbikeprototype;

import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.TimerTask;

import static com.example.berendvet.idbikeprototype.MainActivity.TAG;

public class BicycleAdapter extends TimerTask {

    MainActivity ma;
    public BicycleParameter varp[];     // variable parameter
    public BicycleParameter conp[];     // constant parameter

    public BicycleFrame frame;
    public int rxErrorCount = 0;
    public static final int DISPLAY_TYPE = 4;

    public boolean power;
    public boolean headLight;
    public int driveMode;
    public int userAction;
    public int brake_state;
    public int errorCode;

    public int userActionCount = 0;

    // Constructor
    public BicycleAdapter(MainActivity ma) {
        this.ma = ma;
        BicycleParameter.ma = ma;
        frame = new BicycleFrame();
        power = false;

        varp = new BicycleParameter[13];
        varp[0]  = new BicycleParameter("Speed", "km/h", 0.1);
        varp[1]  = new BicycleParameter("Max", "km/h", 0.1);
        varp[2]  = new BicycleParameter("Ave", "km/h", 0.1);
        varp[3]  = new BicycleParameter("ODO", "m", 1);
        varp[4]  = new BicycleParameter("Trip", "m", 1);
        varp[5]  = new BicycleParameter("Time", "s", 1);
        varp[6]  = new BicycleParameter("Bat", "%", 1);    // msbit is state of charge
        varp[7]  = new BicycleParameter("Ibat", "A", 0.1);
        varp[8]  = new BicycleParameter("Temp", "°C", 0.1);
        varp[9]  = new BicycleParameter("Trq", "Nm", 1);
        varp[10] = new BicycleParameter("x1", "", 1);
        varp[11] = new BicycleParameter("x2", "", 1);
        varp[12] = new BicycleParameter("Text", "", 1);

        conp = new BicycleParameter[92];
        conp[0] = new BicycleParameter("Torque_offset", "Nm", 1);
        conp[1] = new BicycleParameter("RelSoc", "%", 1);             // 1 byte
        conp[2] = new BicycleParameter("AbsSoc", "%", 1);             // 1 byte
        conp[3] = new BicycleParameter("RemCapacity", "mAh", 1);      // 2 bytes
        conp[4] = new BicycleParameter("FullCapacity", "mAh", 1);     // 2 bytes
        conp[5] = new BicycleParameter("CycleCount", "", 1);         // ? bytes
        conp[6] = new BicycleParameter("BattStatVoltage", "mV", 1);   // 2 bytes
        conp[7] = new BicycleParameter("BattStatCurrent", "mA", 1);   // 2 bytes
        conp[8] = new BicycleParameter("LongTermCurrent", "A", 0.1);  // ? bytes
        conp[9] = new BicycleParameter("LongTermCurrent2", "A", 0.1); // ? bytes
        conp[10] = new BicycleParameter("Ahours", "Ah", 0.1);          // ? bytes
        conp[11] = new BicycleParameter("RemainingRange", "km", 0.1);  // ? bytes
        conp[12] = new BicycleParameter("VersionMC", "", 1);
        conp[13] = new BicycleParameter("VersionMCBC", "", 1);
        conp[14] = new BicycleParameter("BattCapacity", "Ah", 0.01);
        conp[15] = new BicycleParameter("BackLightSetting", "", 1);
        conp[16] = new BicycleParameter("MotorWiring", "", 1);
        conp[17] = new BicycleParameter("BackLightSettingLevel", "", 1);
        conp[18] = new BicycleParameter("HeadLightSetting", "", 1);
        conp[19] = new BicycleParameter("HeadLightSettingOn", "", 1);
        conp[20] = new BicycleParameter("HeadLightSettingOff", "", 1);
        conp[21] = new BicycleParameter("HeadLightSettingTime", "", 1);
        conp[22] = new BicycleParameter("Units", "", 1);
        conp[23] = new BicycleParameter("SwitchOffTime", "", 1);
        conp[24] = new BicycleParameter("CurrentLimiter", "", 1);	// new
        conp[25] = new BicycleParameter("LogoOn", "", 1);
        conp[26] = new BicycleParameter("ButtonReverse", "", 1);
        conp[27] = new BicycleParameter("LCDContrast", "", 1);
        conp[28] = new BicycleParameter("WheelSize", "", 1);
        conp[29] = new BicycleParameter("WheelSizeCust", "", 1);
        conp[30] = new BicycleParameter("PulsRev", "", 1);
        conp[31] = new BicycleParameter("MaxCurrent", "", 1);
        conp[32] = new BicycleParameter("SineBLDCDriveMode", "", 1);
        conp[33] = new BicycleParameter("PhaseZero", "", 1);
        conp[34] = new BicycleParameter("PhaseShiftSpeed", "", 1);
        conp[35] = new BicycleParameter("MotorReduction", "", 1);
        conp[36] = new BicycleParameter("MotorPoles", "", 1);
        conp[37] = new BicycleParameter("MotorType", "", 1);
        conp[38] = new BicycleParameter("WalkAssistSpeed", "", 1);
        conp[39] = new BicycleParameter("UserPrograms", "", 1);  conp[39].value = 4;  // number of drive modes
        conp[40] = new BicycleParameter("SwitchOffVoltage", "", 1);
        conp[41] = new BicycleParameter("BattCells", "", 1);
        conp[42] = new BicycleParameter("BattChem", "", 1);
        conp[43] = new BicycleParameter("SensorPlate", "", 1);
        conp[44] = new BicycleParameter("ReverseTMM", "", 1);
        conp[45] = new BicycleParameter("ReverseBrake", "", 1);
        conp[46] = new BicycleParameter("Threshold_0", "", 1);
        conp[47] = new BicycleParameter("Threshold_5", "", 1);
        conp[48] = new BicycleParameter("Threshold_8", "", 1);
        for (int i=0 ; i<8 ; i++) conp[49+i] = new BicycleParameter("Amp_Low["+(i+1)+"]", "A", 1);
        for (int i=0 ; i<8 ; i++) conp[57+i] = new BicycleParameter("Amp_Mid["+(i+1)+"]", "A", 1);
        for (int i=0 ; i<8 ; i++) conp[65+i] = new BicycleParameter("Amp_High["+(i+1)+"]", "A", 1);
        for (int i=0 ; i<8 ; i++) conp[73+i] = new BicycleParameter("Amp_Boost["+(i+1)+"]", "A", 1);
        conp[81] = new BicycleParameter("ConfigName", "", 1);
        conp[82] = new BicycleParameter("ConfigDate", "", 1);
        conp[83] = new BicycleParameter("Customer", "", 1);
        conp[84] = new BicycleParameter("BikeTypeName", "", 1);
        conp[85] = new BicycleParameter("PID_P", "", 1);
        conp[86] = new BicycleParameter("PID_I", "", 1);
        conp[87] = new BicycleParameter("PID_D", "", 1);
        conp[88] = new BicycleParameter("SpeedTimeOut", "", 1);
        conp[89] = new BicycleParameter("TestMode", "", 1);
        conp[90] = new BicycleParameter("TestModeSpeed", "", 1);
        conp[91] = new BicycleParameter("Unknown", "", 1);
    }

    @Override
    public void run() {} // Empty function

    public String byteArrayToString(byte buffer[], int n) {    // Used for debugging purposes
        String s = "";
        for (int i=0 ; i<n ; i++) s += ((buffer[i]<0)? (int)buffer[i]+256 : (int)buffer[i]) + " ";
        return s;
    }

    public void readBuffer(byte buffer[]) {
        byte b;	// one read may contain at most 4096 characters
        int k, cnt = buffer.length;
        // Log.e("Infox", "RX: " + byteArrayToString(buffer, buffer.length));
        for (int i=0 ; i<cnt ; i++) {
            b = buffer[i];
            k = frame.readCharacter(b);
            if (k==1) ProcessMessage();                       // Valid message received
            if (k==-1) {
                rxErrorCount++;                               // Can be used for logging
                if (frame.BTLEFRAME) { frame.index = 1; continue; }
                if (b == frame.SYNCFIELD) {
                    frame.index = 0;
                    frame.rx[0] = 0x55;
                } else frame.index = -1;
            }
        }
    }

    void ProcessMessage() {
        frame.index = (frame.BTLEFRAME)? 1 : -1;   // Reset index of new frame
        Log.i(TAG, "New message: <" + frame.toString()  + ">");
        Log.i(TAG, "4 : " + frame.rx[4]);
        Log.i(TAG, "5 : " + frame.rx[5]);
        switch (frame.rx[4]) {
            case BicycleFrame.DT_DISPLAYTYPE:
                CreateStandardMessage(BicycleFrame.DT_DISPLAYTYPE);
                if (!power) {
                    power = true;
                    // ma.runOnUiThread(new WritePowerStateToView());
                }
                break;
            case BicycleFrame.DT_POWEROFF:
                power = false;
                // ma.runOnUiThread(new WritePowerStateToView());
                return;
            case BicycleFrame.DT_MATRIXSETSPEED:

                varp[0].value = (frame.rx[5]<<8) + frame.rx[6];
                varp[0].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETMAXSPEED:
                varp[1].value = (frame.rx[5]<<8) + frame.rx[6];
                varp[1].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETAVERAGESPEED:
                varp[2].value = (frame.rx[5]<<8) + frame.rx[6];
                varp[2].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETODO:
                varp[3].value = (frame.rx[5]<<24) + (frame.rx[6]<<16) + (frame.rx[7]<<8) + frame.rx[8];
                varp[3].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETTRIP:
                varp[4].value = (frame.rx[5]<<24) + (frame.rx[6]<<16) + (frame.rx[7]<<8) + frame.rx[8];
                varp[4].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETDRIVETIME:
                varp[5].value = frame.rx[5]*3600 + frame.rx[6]*60 + frame.rx[7];
                varp[5].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETBATTERY:
                varp[6].value = frame.rx[5];
                varp[6].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXCURRENT:
                varp[7].value = (frame.rx[5]<<8) + frame.rx[6];
                varp[7].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETTEMP:
                varp[8].value = (frame.rx[5]<<8) + frame.rx[6];
                varp[8].writeValueToView();
                break;
            case BicycleFrame.DT_MATRIXSETTMMVALUE:
                varp[9].value = (frame.rx[5]<<8) + frame.rx[6];
                varp[9].writeValueToView();
                break;
            case BicycleFrame.DT_END:
                varp[10].value = (frame.rx[5]<<24) + (frame.rx[6]<<16) + (frame.rx[7]<<8) + frame.rx[8];
                varp[10].writeValueToView();
                break;
            case BicycleFrame.DT_NEWCOMMAND1:
                varp[11].value = (frame.rx[5]<<24) + (frame.rx[6]<<16) + (frame.rx[7]<<8) + frame.rx[8];
                varp[11].writeValueToView();
                break;
            case BicycleFrame.DT_VARSTRING:
                String s = "";
                for (int i=5 ; i<20 ; i++) { if (frame.rx[i]==0) break; s = s + (char)frame.rx[i]; }
                varp[12].unit = s;
                varp[12].writeStringToView();
                break;
            case BicycleFrame.DT_MATRIXSETTMMOFFSETT:  // Doesn't seem to be present anymore
                conp[0].value = (frame.rx[5]<<8) + frame.rx[6];
                break;
            case BicycleFrame.DT_BRAKESTATE:
                brake_state = frame.rx[5];
                break;
            case BicycleFrame.DT_MATRIXSETERRORCODE:  // Doesn't seem to be present anymore
                errorCode = frame.rx[5];
                break;
            case BicycleFrame.DT_MATRIXGETDRIVEMODE:
                TransmitDriveMode();
                break;
            case BicycleFrame.DT_MATRIXSETDRIVEMODE:
                driveMode = frame.rx[5];
                break;
            case BicycleFrame.DT_MATRIXGETHEADLIGHTSTATE:
                CreateStandardMessage(BicycleFrame.DT_MATRIXGETHEADLIGHTSTATE);
                break;
            case BicycleFrame.DT_MATRIXGETUSERACTION:
                CreateStandardMessage(BicycleFrame.DT_MATRIXGETUSERACTION);
                break;
            case BicycleFrame.DT_MATRIXACKUSERACTION:
                break;
            case BicycleFrame.DT_BATTERY_STATISTICS:
                for (int i=0 ; i<11 ; i++) conp[i+1].value = (frame.rx[5+2*i] << 8) + frame.rx[6+2*i];
                break;
            case BicycleFrame.DT_MATRIX_GETFIELD2:
                CreateSetField2Message(frame.rx[5]);    // This command is not used
                break;
            case BicycleFrame.DT_MATRIX_SETFIELD2:
                int pi; // parameter index
                if (frame.rx[5]<37) {
                    pi = 12 + frame.rx[5];
                    conp[pi].value = (frame.rx[8] << 8) + frame.rx[7];
                }
                else if (frame.rx[5]<41) {
                    pi = 49 + 8 * (frame.rx[5] - 37);
                    for (int i = 0; i < 8; i++) conp[pi+i].value = (frame.rx[8+2*i] << 8) + frame.rx[7+2*i];
                }
                else if (frame.rx[5]<45) {
                    pi = 81 + (frame.rx[5] - 41);
                    s = "";
                    for (int i=0 ; i<32 ; i++) { if (frame.rx[i+7]==0) break; s=s+(char)frame.rx[i+7]; }
                    conp[pi].unit = s;
                }
                else {
                    pi =  85 + (frame.rx[5] - 45);
                    conp[pi].value = (frame.rx[8] << 8) + frame.rx[7];   // different order???
                }
                TransmitAcknowledge(frame.rx[5]);
                break;
        }
    }

    public void setDriveMode(int x) {
        if ((x>=0) && (x<=conp[39].value)) driveMode = x;
    }

    public void resetTrip() {
        userAction = 2; // userAction = 2 at a trip reset
    }

    public void invertHeadLight() {
        headLight = !headLight; // Invert least significant bit of headlight
        userAction = (headLight)? 5 : 6;  // userAction = 5 if light is on
    }

    public void setPower(boolean on) {
        power = on;
        if (!on) {
            userAction = 3;  // userAction = 3 when power goes off
            CreateStandardMessage(frame.DT_MATRIXGETUSERACTION);
        }
        else {
            userAction = 0x30; // self defined userAction to turn display on
            CreateStandardMessage(frame.DT_MATRIXGETUSERACTION);
        }
    }

    public void TransmitDriveMode() {
        byte buf[] = new byte[10];
        buf[0] = 0x55;
        buf[1] = 0;
        buf[2] = 1;
        buf[3] = 3;
        buf[4] = BicycleFrame.DT_MATRIXGETDRIVEMODE;
        buf[5] = (byte) driveMode;
        buf[6] = 5;
        TransmitMessage(buf);
    }

    public void CreateStandardMessage(int frameType) {
        byte buf[] = new byte[10];
        buf[0] = 0x55;
        buf[1] = 0;
        buf[2] = 1;
        buf[3] = 2;
        buf[4] = (byte) frameType;
        switch (frameType) {
            case BicycleFrame.DT_DISPLAYTYPE:  // 0x00
                buf[5] = (byte) DISPLAY_TYPE; break;
            case BicycleFrame.DT_MATRIXGETDRIVEMODE:   // 0x3F
                buf[5] = (byte) driveMode; break;
            case BicycleFrame.DT_MATRIXGETHEADLIGHTSTATE:  // 0x4C
                buf[5] = (byte) ((headLight)? 1 : 0); break;
            case BicycleFrame.DT_MATRIXGETUSERACTION:      // 0x4D
                int x = (byte) userAction;
                if (x==0) {
                    ++userActionCount;
                    if (userActionCount % 3 == 0) x = 12;       // I am alive
                    else if (userActionCount % 10 == 0) x = 8;  // Logging off
                    userAction = x;
                }
                buf[5] = (byte) x;
                userAction = 0;    // Reset userAction
        }
        TransmitMessage(buf);
    }

    public void TransmitMessage(byte buf[]) {
        int li = buf[3] + 4;        // li = last index
        byte crc=1;
        for (int i=3 ; i<li ; i++) crc ^= buf[i];
        buf[li] = crc;
        // Log.i("Infox", "RX: " + frame.toString()  + "   TX: " + byteArrayToString(buf, li+1));
        ma.bike.write(buf, li+1);
    }

    public void TransmitAcknowledge(int param) {
        byte buf[] = new byte[10];
        buf[0] = 0x55;
        buf[1] = 0;
        buf[2] = 1;
        buf[3] = 2;   // Length = 2
        buf[4] = (byte) BicycleFrame.DT_MATRIX_ACKFIELD;
        buf[5] = (byte) param;
        TransmitMessage(buf);
    }

    public void CreateSetField2Message(int setting) {
        byte buf[] = new byte[50];
        buf[0] = 0x55;
        buf[1] = 0;
        buf[2] = 1;
        buf[4] = (byte) BicycleFrame.DT_MATRIX_SETFIELD2;
        buf[5] = (byte) setting;
        if ((setting<37) || (setting>=45)) {
            buf[6] = 2;
            buf[3] = 5;
            int pi = (setting<37)? 12+setting : 40+setting;
            buf[7] = (byte) (conp[pi].value & 0xFF);
            buf[8] = (byte) ((conp[pi].value>>8) & 0xFF);
        }
        else if (setting<41) {  // support levels
            buf[6] = 16;
            buf[3] = 19;
            int pi = 49 + 8 * (setting - 37);
            for (int i = 0; i < 8; i++) {
                buf[2 * i + 7] = (byte) conp[pi++].value;
                buf[2 * i + 8] = 0;
            }
        }
        else {
            buf[6] = 32;
            buf[3] = 35;
            int pi = 81 + 8 * (setting - 41);
            char c[] = conp[pi].unit.toCharArray();
            for (int i=0 ; i<32 ; i++) buf[7+i] = (byte) c[i];
        }
        TransmitMessage(buf);
    }

    public class ShowToastMessage implements Runnable { // Used for debugging
        String message;
        public ShowToastMessage(String message) { this.message = message; }
        public void run() {
            Toast.makeText(ma.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

//    public class WritePowerStateToView implements Runnable {
//        public void run() {
//            if (ma.bcd!=null) ma.bcd.setPower(power);
//        }
//    }

}
