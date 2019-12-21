package com.amazonaws.samples;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.List;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import co.junwei.bswabe.Bswabe;
import co.junwei.bswabe.BswabeCph;
import co.junwei.bswabe.BswabeCphKey;
import co.junwei.bswabe.BswabeElementBoolean;
import co.junwei.bswabe.BswabeMsk;
import co.junwei.bswabe.BswabePrv;
import co.junwei.bswabe.BswabePub;
import co.junwei.bswabe.SerializeUtils;
import co.junwei.cpabe.AESCoder;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.plaf.jpbc.util.Arrays;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class DecryptionComponents {
	private BswabeCph cph;
	private byte[] plt;
	private byte[] cphBuf;
	private byte[] aesBuf;
	DecryptionComponents(BswabeCph c, byte[] p, byte[] cB, byte[] aB) {
		cph = c;
		plt = Arrays.copyOf(p, p.length);
		cphBuf = Arrays.copyOf(cB, cB.length);
		aesBuf = Arrays.copyOf(aB, aB.length);
	}

	public BswabeCph CPH() {
		return cph;
	}
	public byte[] PLT() {
		return plt;
	}
	public byte[] CPHBUF() {
		return cphBuf;
	}
	public byte[] AESBUF() {
		return aesBuf;
	}
}

public class AmazonDynamoDBSample extends JFrame implements ActionListener{

	// GUI Part
	JFrame master_frame = new JFrame();
	JLabel lbl_att_1 = new JLabel("Attribute 1:");
	JLabel lbl_att_2 = new JLabel("Attribute 2:");
	JLabel lbl_att_3 = new JLabel("Attribute 3:");

	JLabel lbl_stu_1 = new JLabel("Entry 1:");
	JTextField tf_att_1 = new JTextField(10);
	JTextField tf_att_2 = new JTextField(10);
	JTextField tf_att_3 = new JTextField(10);
	//	JPasswordField tf_password = new JPasswordField(10);
	JButton btn_login = new JButton("Get Info");
	JPanel panel_1 = new JPanel();
	JPanel panel_2 = new JPanel();
	JPanel panel_3 = new JPanel();
	CardLayout cardLayout = new CardLayout();
	//	JPanel panel_master = new JPanel(new CardLayout());
	static String att_1;
	static String att_2;
	static String att_3;


	public AmazonDynamoDBSample() {
		super("SchoolName Login");

		setLayout(cardLayout);
		setSize(275, 150);
		this.setResizable(false);
		setLocationRelativeTo(null);
		//		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		lbl_att_1.setFont(new Font("Ariel", Font.BOLD, 15));
		lbl_att_2.setFont(new Font("Ariel", Font.BOLD, 15));
		lbl_att_3.setFont(new Font("Ariel", Font.BOLD, 15));


		btn_login.addActionListener(this);

		panel_1.setLayout( new FlowLayout());

		String name1 = "home";
		panel_1.add(lbl_att_1);
		panel_1.add(tf_att_1);
		panel_1.add(lbl_att_2);
		panel_1.add(tf_att_2);
		panel_1.add(lbl_att_3);
		panel_1.add(tf_att_3);
		panel_1.add(btn_login);
		add(panel_1,name1);

		String name2 = "staff";
		add(panel_2,name2);

		String name3 = "student";
		panel_3.add(lbl_stu_1);
		add(panel_3,name3);




	}
	// END GUI

	// AWS PART
	static AmazonDynamoDB client;
	static DynamoDB dynamoDB;

	static Vector<String> list_of_entries = new Vector<String>();
	static Vector<DecryptionComponents> encrypted_entries = new Vector<DecryptionComponents>();
	static Vector<String> list_of_policies = new Vector<String>();



	final static boolean DEBUG = true;

	private static void init() throws Exception {

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);
		}
		client = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion("us-west-2")
				.build();
		dynamoDB = new DynamoDB(client);
	}
	// END AWS

	// CPABE PART
	static String[] attr = { "staff","CS451" };	//prof
	static String[] attr2 = {"123456","CPSC"};	//student
	static String policy_s1 = "123456 CS451 CPSC 2of3 staff 1of2";
	static String policy_s2 = "132054 CS451 CPSC 2of3 staff 1of2";
	static String policy_s3 = "153468 CS451 CPSC 2of3 staff 1of2";
	static String policy_s4 = "197354 CS451 CPSC 2of3 staff 1of2";
	static String policy_s5 = "222222 CS451 CPSC 2of3 staff 1of2";
	static String policy_s6 = "298374 CS451 CPSC 2of3 staff 1of2";
	static String policy_s7 = "456648 CS451 CPSC 2of3 staff 1of2";
	static String policy_s8 = "615847 CS451 CPSC 2of3 staff 1of2";
	static String policy_s9 = "794568 CS451 CPSC 2of3 staff 1of2";
	static String policy_s10 = "987654 CS451 CPSC 2of3 staff 1of2";

	static String tableName = "Classroom";

	static BswabePub pub = new BswabePub();
	static BswabeMsk msk = new BswabeMsk();

	// END CPABE

	// MAIN FUNCTION
	public static void main(String[] args) throws Exception {
		init();
		Bswabe.setup(pub, msk);
		encryptData();
		//GUI
		AmazonDynamoDBSample frame = new AmazonDynamoDBSample();
		frame.setVisible(true);
	}

	public static void encryptData() throws Exception{

		list_of_policies.add(policy_s1);
		list_of_policies.add(policy_s2);
		list_of_policies.add(policy_s3);
		list_of_policies.add(policy_s4);
		list_of_policies.add(policy_s5);
		list_of_policies.add(policy_s6);
		list_of_policies.add(policy_s7);
		list_of_policies.add(policy_s8);
		list_of_policies.add(policy_s9);
		list_of_policies.add(policy_s10);

		Table table = dynamoDB.getTable(tableName);
		
		for(int i = 0; i < list_of_policies.size(); i++) {
			Item item = table.getItem("Student_ID", list_of_policies.get(i).substring(0, 6));

			String data = item.toJSONPretty();
			data = data.replaceAll("\\s+", "");
			data = data.replace("{", "");
			data = data.replace("}", "");
			data = data.replace("\n", " ");
			data = data.replace(",", " ");
			data = data.replace("\"", "");
			data = data.replace(":", ": ");
			list_of_entries.add(data);
		}

		for(int i = 0; i< list_of_policies.size();i++) {
			String policy = list_of_policies.get(i);

			BswabeCph cph;
			BswabeCphKey keyCph;
			byte[] plt;
			byte[] cphBuf;
			byte[] aesBuf;
			String message=list_of_entries.get(i);
			Element m;

			keyCph = Bswabe.enc(pub, policy);
			cph = keyCph.cph;
			m = keyCph.key;

			cphBuf = SerializeUtils.bswabeCphSerialize(cph);

			plt = message.getBytes();
			aesBuf = AESCoder.encrypt(m.toBytes(), plt);

			DecryptionComponents DC= new DecryptionComponents(cph,plt,cphBuf,aesBuf);


			encrypted_entries.add(DC);
		}


	}

	public void decryptData(String[] attr,String user) throws Exception{


		if(user.equalsIgnoreCase("student")) {
			for(int i = 0; i<encrypted_entries.size();i++) {
				BswabePrv prv;

				//generate new private key with user inputed attributes
				prv = Bswabe.keygen(pub, msk, attr);

				// Decrypt

				BswabeCph cph = SerializeUtils.bswabeCphUnserialize(pub, encrypted_entries.get(i).CPHBUF());

				BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);

				if (beb.b) {
					byte[] plt = AESCoder.decrypt(beb.e.toBytes(), encrypted_entries.get(i).AESBUF());
					String s = new String(plt);
					JLabel lbl = new JLabel(s);
					panel_3.add(lbl);
				}
			}
		}
		else if(user.equalsIgnoreCase("staff")) {
			for(int i = 0; i<list_of_entries.size();i++) {
				BswabePrv prv;
				BswabeCph cph;

				//generate new private key with user inputed attributes
				prv = Bswabe.keygen(pub, msk, attr);


				// Decrypt

				cph = SerializeUtils.bswabeCphUnserialize(pub, encrypted_entries.get(i).CPHBUF());

				BswabeElementBoolean beb = Bswabe.dec(pub, prv, cph);
				if (beb.b) {
					byte[] plt = AESCoder.decrypt(beb.e.toBytes(), encrypted_entries.get(i).AESBUF());
					String s = new String(plt);
					JLabel lbl = new JLabel(s);
					panel_2.add(lbl);
				}
			}
		}
		else {
			//do nothing
		}

	}


	/* connect element of array with blank */
	public static String array2Str(String[] arr) {
		int len = arr.length;
		String str = arr[0];

		for (int i = 1; i < len; i++) {
			str += " ";
			str += arr[i];
		}

		return str;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btn_login) {
			att_1 = tf_att_1.getText();
			att_2 = tf_att_2.getText();
			att_3 = tf_att_3.getText();
			String attribute= checkAttribute();

			if(attribute.equalsIgnoreCase("student")) {
				setSize(800,100);
				setLocationRelativeTo(null);
				cardLayout.show(getContentPane(), "student");
			}
			else if(attribute.equalsIgnoreCase("staff")) {
				setSize(800,300);
				setLocationRelativeTo(null);
				cardLayout.show(getContentPane(), "staff");
			}
			else {
				//doo nothing
			}
		}
	}

	public String checkAttribute(){

		if(att_2.equalsIgnoreCase("student")) {
			//function call here student
			studentInfo();
			String panel = "student";
			return panel;
		}
		else if(att_2.equalsIgnoreCase("staff")) {
			//function call here staff
			staffInfo();
			String panel = "staff";
			return panel;
		}
		else {
			//do nothing
		}
		return null;
	}

	public void studentInfo(){

		String[] attribute = {att_2,att_1,att_3};
		try {
			decryptData(attribute,att_2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void staffInfo(){
		String[] attribute = {att_2,att_1,att_3};
		try {
			decryptData(attribute,att_2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
