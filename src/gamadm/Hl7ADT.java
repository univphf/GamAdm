package gamadm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * ******************
 * @author tondeur-h
 * @version mai 2015
 ********************
 */
/**
 * *******************************************
 * Classe de gestion des fichier HL7
 * Spécialisé dans le format ORU
 * Elle permet de genérer un fichier HL7 ORU
 ********************************************
 */
public class Hl7ADT {

 //format du fichier de sortie HL7 ORU vers la base de données.
//ORU out
//MSH10 Message control ID (R)
    String ADTMSH = "MSH|^~\\&|SA_AMCK|SF_MCK|DXSRA|admission|#DATEMSG#|A01|ADT^A01|#NUMMSG#|P|2.3.1||||||8859/1|";
//EVN
    String ADTEVN="EVN|A01|#DATEMSG#||||";
//PID 18 Patient control Account
    String ADTPID = "PID|1||#IPP#^^^IF_MCKN||#NOM#^#PRENOM#^^^#INTIT#^^L|#NOMMAR#|#DDN#|#SEXE#|||#ADR1#^#ADR2#^#VILLE#^^#CP#^#PAYS#|#PAYS#|#TEL#|||U||#IEP#||||||||||#PAYSN#^#PAYSN#||N";
//PV1
    String ADTPV1 = "PV1|1|I|#UF#^#CHAMBRE#^#LIT#|R|||||||||||||||#IEP#||||||||||||||||||||||||| #DDS#||||||#NUMPAS#|A|";
//PV2
    String ADTPV2="PV2|||^^^8^^|||||||||||||||||||N";
//ZFU
    String ADTZFU="ZFU|#UF#|#DDS#|#UFMED#|#DDS#|#UF#|#DDS#";
//ZRE
    String ADTZRE="ZRE|||||||||";

      //definir format d'un message ADT^A01
    //MSH|^~\&|SA_AMCK|SF_MCK|DXSRA|admission|#DATEMSG#|A01|ADT^A01|#NUMMSG#|P|2.3.1||||||8859/1|
    //EVN|A01|#DATEMSG#||||
    //PID|1||#IPP#^^^IF_MCKN||#NOM#^#PRENOM#^^^#INTIT#^^L|#NOMMAR#|#DDN#|#SEXE#|||#ADR1#^#ADR2#^#VILLE#^^#CP#^#PAYS#|#PAYS#|#TEL#|||U|| #IEP#||||| |||||#PAYSN#^#PAYSN#||N
    //PV1|1|I| #UF#^#CHAMBRE#^#LIT#|R||||||||||||||| #IEP#||||||||||||||||||||||||| #DDS#||||||#NUMPAS#|A|
    //PV2|||^^^8^^|||||||||||||||||||N
    //ZFU| #UF#| #DDS#|#UFMED#| #DDS#| #UF#| #DDS#
    //ZRE|||||||||

    /*****************************
     * VARIABLES GLOBALES
     ****************************/
    //identité patient et data patient
    private String DATEMSG;//now
    private String NUMMSG;//auto
    private String IPP;//
    private String NOM;//
    private String PRENOM;//
    private String INTIT;
    private String NOMMAR;//
    private String DDN;//
    private String SEXE;//
    private String IEP;//
    private String ADR1;
    private String ADR2;
    private String CP;
    private String VILLE;
    private String PAYS;
    private String TEL;//
    private String PAYSN;
    private String DDS;
    private String UF;
    private String CHAMBRE;
    private String LIT;
    private String UFMED;
    private String NUMPAS;

    //newlines
    final int CR = 13;
    final int LF = 10;


    //logger par les api java
    //static final Logger logger = Logger.getLogger(Hl7ADT.class.getName());


    /*************************************************************
     * constructeur initialisation
     * initialise surtout les variables
     * @param IPP
     * @param NOM
     * @param PRENOM
     * @param INTIT
     * @param NOMMAR
     * @param DDN
     * @param SEXE
     * @param DDS
     * @param PAYS
     * @param IEP
     * @param ADR2
     * @param ADR1
     * @param VILLE
     * @param UFMED
     * @param CP
     * @param PAYSN
     * @param TEL
     * @param CHAMBRE
     * @param UF
     * @param LIT
     *************************************************************/
    public Hl7ADT(String IPP,String NOM, String PRENOM, String INTIT,String NOMMAR, String DDN, String SEXE, String IEP, String ADR1, String ADR2, String CP, String VILLE, String PAYS, String TEL, String PAYSN, String DDS, String UF, String CHAMBRE, String LIT,String UFMED) {
        //parametrage du logger pour generer des logs
//        try
//        {
//            logger.setUseParentHandlers(false);
//            FileHandler fh = new FileHandler("CHVDemat%u.log", 0, 1, true);
//            fh.setFormatter(new SimpleFormatter());
//            logger.addHandler(fh);
//        } catch (IOException ioe) {
//            //erreur silencieuse...
//        }
        //definir ici les numero auto et dates auto
          //String DATEMSG;//now
        NUMMSG=String.valueOf(System.currentTimeMillis());
        NUMPAS=String.valueOf(System.currentTimeMillis()-100);
        Calendar c=Calendar.getInstance();
        this.IPP=IPP;
        this.NOM = NOM;
        this.PRENOM = PRENOM;
        this.INTIT=INTIT;
        this.NOMMAR = NOMMAR;
        this.DDN=DDN;
        this.SEXE = SEXE;
        this.IEP = IEP;
        this.ADR1=ADR1;
        this.ADR2=ADR2;
        this.CP=CP;
        this.VILLE=VILLE;
        this.PAYS=PAYS;
        this.TEL=TEL;
        this.PAYSN=PAYSN;
        this.DDS=DDS+" "+completeZero(c.get(Calendar.HOUR_OF_DAY))+":"+completeZero(c.get(Calendar.MINUTE));
        this.UF = UF;
        this.CHAMBRE=CHAMBRE;
        this.LIT=LIT;
        this.UFMED=UFMED;
        this.DATEMSG=LocalDate.now().toString()+" "+completeZero(c.get(Calendar.HOUR_OF_DAY))+":"+completeZero(c.get(Calendar.MINUTE));
    }


    private String completeZero(int in)
    {
        String out=""+in;
        if (out.length()<2){ out="0"+in;}
        return out;
    }

    /*****************************************************
     * convertir une date JJ/MM/AAAA en date hl7 AAAAMMJJ
     *
     *
     * @param dateSimple String
     * @return String
     *****************************************************/
    private String Date_to_Date_HL7_court(String dateSimple) {
//parser la date
        String JJ = dateSimple.substring(8, 10);
        String MM = dateSimple.substring(5, 7);
        String AAAA = dateSimple.substring(0, 4);
        return AAAA + MM + JJ + "000000";
    }


    /*********************************************************************
     * convertir une date JJ/MM/AAAA HH:MM:SS en date hl7 AAAAMMJJHHMMSS
     *
     *
     * @param dateComplexe String
     * @return String
     ********************************************************************/
    private String Date_to_Date_HL7_long(String dateComplexe) {
    //parser la date
        String JJ = dateComplexe.substring(8, 10);
        String MM = dateComplexe.substring(5, 7);
        String AAAA = dateComplexe.substring(0, 4);
        String HH = dateComplexe.substring(11, 13);
        String mm = dateComplexe.substring(14, 16);
        return AAAA + MM + JJ + HH + mm + "00";
    }


    /*********************************************************************
     * Fonction de remplacement des tag #XXXX# pour les segments HL7
     *
     * @param in (Chaine de caratere du segment a remplacer
     * @return String
     * @throws java.lang.Exception Erreur replaceAll
     *********************************************************************/
    private String hl7(String in) throws Exception {
        String outS = in;
//Segment MSH
        outS = outS.replaceAll("#DATEMSG#", Date_to_Date_HL7_long(DATEMSG));
        outS = outS.replaceAll("#NUMMSG#", NUMMSG);
        outS = outS.replaceAll("#NUMPAS#", NUMPAS);
        outS = outS.replaceAll("#DATEMSG#", Date_to_Date_HL7_long(DDS));
//segment PID/PV1
        outS = outS.replaceAll("#INTIT#", INTIT);
        outS = outS.replaceAll("#NOM#", NOM);
        outS = outS.replaceAll("#NOMMAR#", NOMMAR);
        outS = outS.replaceAll("#PRENOM#", PRENOM);
        outS = outS.replaceAll("#SEXE#", SEXE);
        outS = outS.replaceAll("#DDN#", Date_to_Date_HL7_court(DDN));
        outS = outS.replaceAll("#ADR1#", ADR1);
        outS = outS.replaceAll("#ADR2#", ADR2);
        outS = outS.replaceAll("#CP#", CP);
        outS = outS.replaceAll("#VILLE#", VILLE);
        outS = outS.replaceAll("#PAYS#", PAYS);
        outS = outS.replaceAll("#PAYSN#", PAYSN);
        outS = outS.replaceAll("#IPP#", IPP);
        outS = outS.replaceAll("#IEP#", IEP);
        outS = outS.replaceAll("#UF#", UF);
        outS = outS.replaceAll("#CHAMBRE#", CHAMBRE);
        outS = outS.replaceAll("#LIT#", LIT);
        outS = outS.replaceAll("#TEL#",TEL);
//segment ZFU
        outS = outS.replaceAll("#UFMED#", UFMED);
        outS = outS.replaceAll("#DDS#",Date_to_Date_HL7_long(DDS));



        return outS;
    }


    /***************************************************
     * Procedure qui permet d'ecrire le fichier hl7_oru
     *
     *
     * @param outputFileName String
     * @return boolean
     * @throws java.lang.Exception Erreur d'ecriture sur disque
     ***************************************************/
    public boolean create_adt(String outputFileName) throws Exception {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(outputFileName));
            out.write(hl7(ADTMSH));
            out.write(CR);
            out.write(hl7(ADTEVN));
            out.write(CR);
            out.write(hl7(ADTPID));
            out.write(CR);
            out.write(hl7(ADTPV1));
            out.write(CR);
            out.write(hl7(ADTPV2));
            out.write(CR);
            out.write(hl7(ADTZFU));
            out.write(CR);
            out.write(hl7(ADTZRE));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return true;
    } //fin create_adt


    /**********************************************
     * Creer un fichier verrou avec extension .ok seulement si demandé dans le
     * parametrage
     *
     * @param outputFileName String
     * @return boolean
     * @throws Exception Erreur d'ecriture sur disque
     **********************************************/
    public boolean create_OK(String outputFileName) throws Exception {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(outputFileName));
            out.print(outputFileName);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return true;
    } //fin create_OK

} //fin de la classe
