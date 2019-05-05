package gamadm;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 *
 * @author maj
 */
public class FXMLGAMController implements Initializable {

    //definir format d'un message ADT^A01
    //MSH|^~\&|SA_AMCK|SF_MCK|DXSRA|admission|#DATEMSG#|A01|ADT^A01|#NUMMSG#|P|2.3.1||||||8859/1|
    //EVN|A01|#DATEMSG#||||
    //PID|1||#IPP#^^^IF_MCKN||#NOM#^#PRENOM#^^^#INTIT#^^L|#NOMMAR#|#DDN#|#SEXE#|||#ADR1#^#ADR2#^#VILLE#^^#CP#^#PAYS#|#PAYS#|#TEL#|||U|| #IEP#||||| |||||#PAYSN#^#PAYSN#||N
    //PV1|1|I| #UF#^#CHAMBRE#^#LIT#|R||||||||||||||| #IEP#||||||||||||||||||||||||| #DDS#||||||#NUMPAS#|A|
    //PV2|||^^^8^^|||||||||||||||||||N
    //ZFU| #UF#| #DDS#|#UFMED#| #DDS#| #UF#| #DDS#
    //ZRE|||||||||

    @FXML
    private Button btnQuitter;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfNomMar;
    @FXML
    private DatePicker dpDDN;
    @FXML
    private TextField tfPrenom;
    @FXML
    private TextField tfIPP;
    @FXML
    private DatePicker dpDateAdmission;
    @FXML
    private ChoiceBox<Service> cbService;
    @FXML
    private ComboBox<Sexe> cbSexe;
    @FXML
    private ComboBox<Intitule> cbIntitule;
    @FXML
    private ComboBox<Nationalite> cbNationalite;
    @FXML
    private TextField tfChambre;
    @FXML
    private TextField tfLit;
    @FXML
    private ComboBox<Service> cbUFMED;
    @FXML
    private TextField tfAdresse1;
    @FXML
    private TextField tfAdresse2;
    @FXML
    private TextField tfVille;
    @FXML
    private TextField tfCP;
    @FXML
    private TextField tfTel;
    @FXML
    private ComboBox<Pays> cmPays;
    @FXML
    private Button btnValider;
    @FXML
    private Button btnAdmission;


    ObservableList<Sexe> obSexe=javafx.collections.FXCollections.observableArrayList();
    ObservableList<Intitule> obIntitule=javafx.collections.FXCollections.observableArrayList();
    ObservableList<Nationalite> obNationalite=javafx.collections.FXCollections.observableArrayList();
    ObservableList<Service> obService=javafx.collections.FXCollections.observableArrayList();
    ObservableList<Pays> obPays=javafx.collections.FXCollections.observableArrayList();

    Hl7ADT hl7;
    final private String QUEUE_NAME="GAMADM";
    String message=null;
    Channel channel;
    Connection conn;
    AMQP.BasicProperties props;

     private final String propFile="GamAdm.properties";

     boolean rabbit=false;
     String rabbitIP="localhost";
     int rabbitPort=5672;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ajouter les champs combo
        obSexe.add(new Sexe("Masculin","M"));
        obSexe.add(new Sexe("Feminin","F"));
        cbSexe.setItems(obSexe);
        //ajouter intitule
        obIntitule.add(new Intitule("Mr", "Mr"));
        obIntitule.add(new Intitule("Mme", "Mme"));
        obIntitule.add(new Intitule("Mle", "Mle"));
        cbIntitule.setItems(obIntitule);
        //ajouter Nationalité
        obNationalite.add(new Nationalite("Française","100"));
        obNationalite.add(new Nationalite("Anglaise","108"));
        obNationalite.add(new Nationalite("Italienne","145"));
        obNationalite.add(new Nationalite("Allemande","178"));
        obNationalite.add(new Nationalite("Belge","109"));
        cbNationalite.setItems(obNationalite);
        //ajouter UF et UFMed
        obService.add(new Service("Urgences", "0991"));
        obService.add(new Service("Neurologie", "1151"));
        obService.add(new Service("Chirurgie", "2121"));
        cbService.setItems(obService);
        cbUFMED.setItems(obService);
        //ajouter pays
        obPays.add(new Pays("FRANCE", "100"));
        obPays.add(new Pays("ANGLETERRE", "108"));
        obPays.add(new Pays("ITALIE", "145"));
        obPays.add(new Pays("ALLEMAGNE", "178"));
        obPays.add(new Pays("BELGE", "109"));
        cmPays.setItems(obPays);

        //fixer les éléments
        dpDDN.setValue(LocalDate.of(1971, 01, 01));
        dpDateAdmission.setValue(LocalDate.now());
        cmPays.getSelectionModel().select(0);
        cbIntitule.getSelectionModel().select(0);
        cbNationalite.getSelectionModel().select(0);
        cbService.getSelectionModel().select(0);
        cbSexe.getSelectionModel().select(0);
        cbUFMED.getSelectionModel().select(0);

        //fixer les dates
        dpDDN.setValue(LocalDate.of(1970, 01, 01));
        dpDateAdmission.setValue(LocalDate.now());

        disabledWidget(true);

        lire_properties();

        if (rabbit) connecter_rabbitMQ();

    }


    @FXML
    private void hbtnQuitter(ActionEvent event) {
        if (rabbit) deconnecter_RabbitMQ();
        Platform.exit();
    }


    String intitule;
    String sexe;
    String nationalite;
    String pays;
    String serviceMed;
    String service;
    String iep;

    @FXML
    private void hbtnValider(ActionEvent event) {
        try {
            //generer le message dans le dossier courant de l'application...

            intitule=cbIntitule.getSelectionModel().getSelectedItem().getCode();
            sexe=cbSexe.getSelectionModel().getSelectedItem().getCode();
            nationalite=cbNationalite.getSelectionModel().getSelectedItem().getCode();
            pays=cmPays.getSelectionModel().getSelectedItem().getCode();
            serviceMed=cbUFMED.getSelectionModel().getSelectedItem().getCode();
            service=cbService.getSelectionModel().getSelectedItem().getCode();
            iep=String.valueOf((int)(Math.random()*999999999));

            hl7=new Hl7ADT(tfIPP.getText(), tfNom.getText(), tfPrenom.getText(),intitule, tfNomMar.getText(), dpDDN.getValue().toString(), sexe, iep, tfAdresse1.getText(),tfAdresse2.getText(), tfCP.getText(), tfVille.getText(), pays, tfTel.getText(), nationalite, dpDateAdmission.getValue().toString()  ,service,tfChambre.getText(),tfLit.getText(), serviceMed);

            hl7.create_adt(System.currentTimeMillis()+".hl7");

            if (rabbit) send_Message();

            btnValider.setDisable(true);
            disabledWidget(true);

        } catch (Exception ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void hbtnAdmission(ActionEvent event) {
        //supprimer tous les champs
        tfIPP.clear();
        tfNom.clear();
        tfNomMar.clear();
        tfPrenom.clear();
        tfAdresse1.clear();
        tfAdresse2.clear();
        tfCP.clear();
        tfChambre.clear();
        tfLit.clear();
        tfTel.clear();
        tfVille.clear();
        dpDDN.setValue(LocalDate.of(1971, 01, 01));
        dpDateAdmission.setValue(LocalDate.now());
        cmPays.getSelectionModel().select(0);
        cbIntitule.getSelectionModel().select(0);
        cbNationalite.getSelectionModel().select(0);
        cbService.getSelectionModel().select(0);
        cbSexe.getSelectionModel().select(0);
        cbUFMED.getSelectionModel().select(0);

        btnValider.setDisable(false);
        disabledWidget(false);
    }


    private void disabledWidget(boolean enable)
    {
        tfIPP.setDisable(enable);
        tfAdresse1.setDisable(enable);
        tfAdresse2.setDisable(enable);
        tfCP.setDisable(enable);
        tfChambre.setDisable(enable);
        tfLit.setDisable(enable);
        tfNom.setDisable(enable);
        tfNomMar.setDisable(enable);
        tfPrenom.setDisable(enable);
        tfTel.setDisable(enable);
        tfVille.setDisable(enable);
        dpDDN.setDisable(enable);
        dpDateAdmission.setDisable(enable);
        cmPays.setDisable(enable);
        cbIntitule.setDisable(enable);
        cbNationalite.setDisable(enable);
        cbService.setDisable(enable);
        cbSexe.setDisable(enable);
        cbUFMED.setDisable(enable);
    }


    /**************************************
    Connecter l'applaciation vers RabbitMQ
    ***************************************/
    private void connecter_rabbitMQ() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitIP);
            factory.setPort(rabbitPort);

            conn=factory.newConnection();

            channel=conn.createChannel();

            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

            builder.deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode());
            builder.priority(MessageProperties.PERSISTENT_TEXT_PLAIN.getPriority());
            builder.contentType("application/txt");
            builder.appId("GamAdm");

            props=builder.build();

            channel.queueDeclare(QUEUE_NAME, false, false, false,null);

        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Désolé, je n'arrive pas a contacter RabbitMQ sur "+rabbitIP+" et le port "+rabbitPort+"...\r\n Vérifiez le lancement de RabbitMQ...");
            alert.showAndWait();
            Platform.exit();
        }

    }

    /*************************************
     * Send message
     *************************************/
    private void send_Message(){
        try {
            message=tfIPP.getText()+"#"+tfNom.getText()+"#"+tfPrenom.getText()+"#"+intitule+"#"+tfNomMar.getText()+"#"+dpDDN.getValue().toString()+"#"+sexe+"#"+iep+"#"+tfAdresse1.getText()+"#"+tfAdresse2.getText()+"#"+tfCP.getText()+"#"+tfVille.getText()+"#"+pays+"#"+tfTel.getText()+"#"+nationalite+"#"+dpDateAdmission.getValue().toString()+"#"+service+"#"+tfChambre.getText()+"#"+tfLit.getText()+"#"+serviceMed;
            channel.basicPublish("",QUEUE_NAME, props,message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***********************************
     * Fermer connection avec RabbitMQ
     ************************************/
    private void deconnecter_RabbitMQ() {
        try {
            channel.close();
            conn.close();
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /*************************************
     * Lire le fichier properties
     *************************************/
    private void lire_properties() {
      FileReader f=null;
        try {
            f = new FileReader(propFile);
            Properties p=new Properties();
            p.load(f);
            String rabbitStr=p.getProperty("rabbit", "true");
             rabbit=Boolean.valueOf(rabbitStr);
             rabbitIP=p.getProperty("interface", "localhost");
             rabbitPort=Integer.parseInt(p.getProperty("port", "5672"),10);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(FXMLGAMController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }



}
