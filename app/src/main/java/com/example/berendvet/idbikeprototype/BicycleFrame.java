package com.example.berendvet.idbikeprototype;

/**
 * Created by i5 on 8-3-2015.
 */
public class BicycleFrame {

    int index;
    int errorCount;
    int rx[];

    final static boolean BTLEFRAME = true;

    final static int SYNCFIELD = 0x55;
    final static int DT_DISPLAYTYPE = 0;
    final static int DT_SWITCHES = 1;
    final static int DT_BATTLEVEL = 2;
    final static int DT_PROGRAM = 3;
    final static int DT_LIGHT = 4;
    final static int DT_POWEROFF = 5;
    final static int DT_ACK = 6;
    final static int DT_SPEED = 7;
    final static int DT_TOTALLARGE = 8;
    final static int DT_TOTALSMALL = 9;
    final static int DT_TIME = 10;
    final static int DT_AVG = 11;
    final static int DT_WHEEL = 12;
    final static int DT_NOMENU =  13;
    final static int DT_TOTALTIME = 14;
    final static int DT_ICONS = 15;
    final static int DT_BATTERYMENU=16;
    final static int DT_POWERON = 17;
    final static int DT_DISPLAYCLR = 18;
    final static int DT_SPEED_ML = 19;
    final static int DT_TOTALLARGE_ML = 20;
    final static int DT_TOTALSMALL_ML = 21;
    final static int DT_VERSION = 22;
    final static int DT_ERROR = 23;
    final static int DT_NOMENU_ML = 24;
    final static int DT_700C = 25;
    final static int DT_INCH = 26;
    final static int DT_OUTPUTBAR = 27;
    final static int DT_CALIBRATE = 28;
    final static int DT_MAXSPEED = 29;
    // controller messages
    final static int DT_SETBAUDRATE = 30;
    final static int DT_GETVERSIONSOFTW = 31;
    final static int DT_GETVERSIONHARDW = 32;
    final static int DT_GETDATETIME = 33;
    final static int DT_SETDATETIME = 34;
    final static int DT_SETMOTOR = 35;
    final static int DT_GETCURRENT = 36;
    final static int DT_GETSPEED = 37;
    final static int DT_SETLAMP = 38;
    final static int DT_GETBREAK = 39;
    final static int DT_GETHALL = 40;
    final static int DT_GETTOTALKM = 41;
    final static int DT_GETTEMPHDW = 42;
    final static int DT_GETTEMPMOTOR = 43;
    final static int DT_GETBATTLEVEL = 44;
    final static int DT_GETBUSVOLTAGE = 45;
    final static int DT_GETTMM = 46;
    final static int DT_GETTMMOFFSETT = 47;
    final static int DT_STARTINTERPRETER = 48;
    final static int DT_GETSERIAL = 49;
    final static int DT_SETSERIAL = 50;
    final static int DT_RESET = 51;
    final static int DT_WRITERECORD = 52;
    final static int DT_READRECORD = 53;
    final static int DT_ERASEDISK = 54;
    final static int DT_NACK = 55;
    final static int DT_GETVERSIONBC = 56;
    final static int DT_GETCOMPILEDATE = 57;
    final static int DT_GETCOMPILETIME = 58;
    final static int DT_FEET = 59;
    final static int DT_BIKETYPE = 60;
    final static int DT_LOGGINGON = 61;
    // new data for IDBMatrix6
    final static int DT_MATRIXSETSPEED = 0x3e;
    final static int DT_MATRIXGETDRIVEMODE = 0x3f;
    final static int DT_MATRIXSETDRIVEMODE = 0x40;
    final static int DT_MATRIXSETODO = 0x42;        // reversed with original protocol
    final static int DT_MATRIXSETTRIP = 0x41;       // reversed with original protocol
    final static int DT_MATRIXSETBATTERY = 0x43;
    final static int DT_MATRIXSETMAXSPEED = 0x44;
    final static int DT_MATRIXSETAVERAGESPEED = 0x45;
    final static int DT_MATRIXSETDRIVETIME = 0x46;
    final static int DT_MATRIXPOWEROFFCONTROLLER = 0x47;
    final static int DT_MATRIXSETERRORCODE = 0x48;
    final static int DT_MATRIXSTARTCALIBRATION = 0x49;
    final static int DT_MATRIXSETTMMVALUE = 0x4a;
    final static int DT_MATRIXGETDAYLIGHTLEVEL = 0x4b;
    final static int DT_MATRIXGETHEADLIGHTSTATE = 0x4c;
    final static int DT_MATRIXGETUSERACTION = 0x4d;
    final static int DT_MATRIXSETTMMOFFSETT = 0x4e;
    final static int DT_MATRIXCURRENT = 0x4f;
    final static int DT_MATRIXSETTEMP = 0x50;
    final static int DT_MATRIXACKUSERACTION = 0x51;

    final static int DT_END = 0x52;
    final static int DT_NEWCOMMAND1 = 0x53;
    final static int DT_VARSTRING = 0x54;

    final static int DT_BRAKESTATE = 0x60;
    final static int DT_MATRIX_GETFIELD2 = 0x90;
    final static int DT_MATRIX_SETFIELD2	= 0x91;
    final static int DT_MATRIX_NACKFIELD = 0x93;
    final static int DT_MATRIX_ACKFIELD = 0x94;
    final static int DT_BATTERY_STATISTICS = 0xA0;

    public BicycleFrame() {
        rx = new int[40];
        index = (BTLEFRAME)? 1 : -1;     // index is the index of the last received byte.
        errorCount = 0;
        if (BTLEFRAME) { rx[0] = 0x55; rx[1] = 1; } // These are not present in btle-frames
    }

    public String toString() {
        String s = "";
        int cnt = rx[3]+5;
        if (cnt>40) return "Message too large: " + cnt;
        for (int i=0 ; i<cnt ; i++) {
            int v = rx[i] & 0xFF;
            s += MainActivity.hexArray[v >>> 4];
            s += MainActivity.hexArray[v & 0x0F];
            s += '-';
            // s = s + rx[i] + " ";
        }
        return s;
    }

    // The function readCharacter(byte c) returns -1 if an invalid character is received.
    // The function returns 1 if a complete frame is received, otherwise it returns 0

    public int readCharacter(byte c) {
        if (index>=39) return -1;   // Maximum number of bytes is 40, maximum index is 39
        rx[++index] = c;            // Increase index and store data
        if (rx[index]<0) rx[index] += 256;  // JAVA does not support unsigned integers...
        switch (index) {            // Examine byte
            case 0:  return (c == SYNCFIELD)? 0 : -1;
            case 1:  return (c == 1)? 0 : -1;
            case 2:  return (c == 0)? 0 : -1;
            case 3:  return ((c > 0) && (c <= 35))? 0 : -1;
            case 4:  int len = GetFrameLength();
                if (BTLEFRAME && (rx[3] == 1))return 1;
                return ((len == 0) || (len == (rx[3] + 5)))? 0 : -1;
            default:
                if (BTLEFRAME && (index == (rx[3] + 3))) return 1;
                if (index == (rx[3] + 4)) return (c==CalculateCRC())? 1 : -1; // Only applicable for complete frames.
                if (rx[4] == DT_MATRIX_SETFIELD2) {
                    if (index == 5) return (rx[5] < 52)? 0 : -1;
                    if (index == 6) if (c != GetParameterLength()) return -1;
                }
                return 0;
        }
    }

    public byte CalculateCRC() {
        byte crc = 1;
        for (int i=3 ; i<index ; i++) crc ^= rx[i];
        return crc;
    }

    public int GetParameterLength() {
        if (rx[5]<37) return 2;
        if (rx[5]<41) return 16;
        if (rx[5]<45) return 32;
        if (rx[5]<52) return 2;
        return -1;
    }

    // The whole framelength is returned including the sof and crc
    public int GetFrameLength() {
        switch (rx[4]) {
            case DT_DISPLAYTYPE: return 7;
            case DT_SWITCHES: return 0;
            case DT_BATTLEVEL: return 0;
            case DT_PROGRAM: return 0;
            case DT_LIGHT: return 0;
            case DT_POWEROFF: return 0;
            case DT_ACK: return 0;
            case DT_SPEED: return 0;
            case DT_TOTALLARGE: return 0;
            case DT_TOTALSMALL: return 0;
            case DT_TIME: return 0;
            case DT_AVG: return 0;
            case DT_WHEEL: return 0;
            case DT_NOMENU: return 0;
            case DT_TOTALTIME: return 0;
            case DT_ICONS: return 0;
            case DT_BATTERYMENU: return 0;
            case DT_POWERON: return 0;
            case DT_DISPLAYCLR: return 0;
            case DT_SPEED_ML: return 0;
            case DT_TOTALLARGE_ML: return 0;
            case DT_TOTALSMALL_ML: return 0;
            case DT_VERSION: return 0;
            case DT_ERROR: return 0;
            case DT_NOMENU_ML: return 0;
            case DT_700C: return 0;
            case DT_INCH: return 0;
            case DT_OUTPUTBAR: return 0;
            case DT_CALIBRATE: return 0;
            case DT_MAXSPEED: return 0;
            case DT_SETBAUDRATE: return 0;
            case DT_GETVERSIONSOFTW: return 0;
            case DT_GETVERSIONHARDW: return 0;
            case DT_GETDATETIME: return 0;
            case DT_SETDATETIME: return 0;
            case DT_SETMOTOR: return 0;
            case DT_GETCURRENT: return 0;
            case DT_GETSPEED: return 0;
            case DT_SETLAMP: return 0;
            case DT_GETBREAK: return 0;
            case DT_GETHALL: return 0;
            case DT_GETTOTALKM: return 0;
            case DT_GETTEMPHDW: return 0;
            case DT_GETTEMPMOTOR: return 0;
            case DT_GETBATTLEVEL: return 0;
            case DT_GETBUSVOLTAGE: return 0;
            case DT_GETTMM: return 0;
            case DT_GETTMMOFFSETT: return 0;
            case DT_STARTINTERPRETER: return 0;
            case DT_GETSERIAL: return 0;
            case DT_SETSERIAL: return 0;
            case DT_RESET: return 0;
            case DT_WRITERECORD: return 0;
            case DT_READRECORD: return 0;
            case DT_ERASEDISK: return 0;
            case DT_NACK: return 0;
            case DT_GETVERSIONBC: return 0;
            case DT_GETCOMPILEDATE: return 0;
            case DT_GETCOMPILETIME: return 0;
            case DT_FEET: return 0;
            case DT_BIKETYPE: return 0;
            case DT_LOGGINGON: return 0;
            case DT_MATRIXSETSPEED: return 10;
            case DT_MATRIXGETDRIVEMODE: return 6;
            case DT_MATRIXSETDRIVEMODE: return 8;
            case DT_MATRIXSETODO: return 10;
            case DT_MATRIXSETTRIP: return 10;
            case DT_MATRIXSETBATTERY: return 7;
            case DT_MATRIXSETMAXSPEED: return 10;
            case DT_MATRIXSETAVERAGESPEED: return 10;
            case DT_MATRIXSETDRIVETIME: return 9;
            case DT_MATRIXPOWEROFFCONTROLLER: return 0;
            case DT_MATRIXSETERRORCODE: return 7;
            case DT_MATRIXSTARTCALIBRATION: return 0;
            case DT_MATRIXSETTMMVALUE: return 8;
            case DT_MATRIXGETDAYLIGHTLEVEL: return 0;
            case DT_MATRIXGETHEADLIGHTSTATE: return 6;
            case DT_MATRIXGETUSERACTION: return 6;
            case DT_MATRIXSETTMMOFFSETT: return 10;
            case DT_MATRIXCURRENT: return 10;
            case DT_MATRIXSETTEMP: return 10;
            case DT_MATRIXACKUSERACTION: return 8;

            case DT_END: return 10;
            case DT_NEWCOMMAND1: return 10;
            case DT_VARSTRING: return 23;

            case DT_BRAKESTATE: return 10;
            case DT_MATRIX_GETFIELD2: return 0;
            case DT_MATRIX_SETFIELD2: return 0; // parameter setting, variable field
            case DT_MATRIX_NACKFIELD: return 0;
            case DT_MATRIX_ACKFIELD: return 7;
            case DT_BATTERY_STATISTICS: return 28;
            default: return 0;
        }
    }

}

/*
enum    _UserActions {
    UA_NONE,            // 0 niks aan de hand..
    UA_ERROR_RESET,     // 1 reset de fout in de controller
    UA_COUNTER_RESET,   // 2 reset de trip counter, reset de km dagteller
    UA_POWER_OFF,       // 3 zet mij uit…
    UA_TMM_CALIBRATE,   // niet geimplementeerd
    UA_LIGHT_ON,        // 5 koplamp aan
    UA_LIGHT_OFF,       // 6 koplamp uit
    UA_LOGGING_ON,      // 7 zet highspeed logging aan
    UA_LOGGING_OFF,     // 8 Zet de log mode uit
    UA_SAVESETTINGS,    // 9 settings zijn gewijzigd, haal ze op
    UA_RELOADSETTINGS,  // 10 geef me alle settings
    UA_INMENU,          // ik zit in het instellings menu
    UA_KEEPON,          // 12 keep on, motor controller ik ben er nog
    UA_RANGERESET,      // reset de accu range
    UA_WALKASSISTON,    // zet de ‘aan de hand loop mode aan’
    UA_WALKASSISTOFF,   // zet weer uit
    UA_WALKASSISTONREV, // aan de hand loop mode aan in de reverse.
    UA_POWER_ON = 0x7C  // toegevoegd door SKBS
    UA_END
};
 */