package de.hshannover.inform.trust.ifmapj.ironcontrol.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import android.os.Environment;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
import de.hshannover.inform.trust.ifmapj.ironcontrol.view.MainActivity;

public class KeystoreManager {
	/**
	 * Class for connection management
	 * @author Anton Saenko
	 * @since 1.0
	 */
	private static final Logger logger = LoggerFactory.getLogger(KeystoreManager.class);
	private static String PATH_KEYSTORE_DIR = Environment.getExternalStorageDirectory().getPath() +"/ironcontrol/keystore";
	private static String PATH_CERT_DIR = Environment.getExternalStorageDirectory().getPath() +"/ironcontrol/certificates";
	private static String PATH_TO_DEF_KEYSTORE =  PATH_KEYSTORE_DIR +"/ironcontrol.bks";
	private static String PATH_TO_DEF_CERT =  PATH_KEYSTORE_DIR +"/ironcontrol.pem";
	private static File dirKeystore, dirCertificate, keystore, certificate;

	public static void checkANDcreateSDCardFolder(){
		if(isSDMounted()){
			dirKeystore = new File(PATH_KEYSTORE_DIR);
			keystore = new File(PATH_TO_DEF_KEYSTORE);
			certificate = new File(PATH_TO_DEF_CERT);
			dirCertificate = new File(PATH_CERT_DIR);
			if(!dirKeystore.exists()&& !dirKeystore.isDirectory()){
				dirKeystore.mkdirs();
				logger.debug(PATH_KEYSTORE_DIR + " created!");
			}
			if(!dirCertificate.exists()&& !dirCertificate.isDirectory()){
				dirCertificate.mkdirs();
				logger.debug(PATH_CERT_DIR + " created!");
			}
			if(!keystore.exists() || !certificate.exists()){
				copyDefaultToSD();
				logger.debug(PATH_TO_DEF_KEYSTORE +" crated !");
				logger.debug(PATH_TO_DEF_CERT +" crated !");
			}
		}else{
			logger.warn(Environment.getExternalStorageDirectory().toString() +"State: "+ Environment.getExternalStorageState() );
		}
	}

	/**
	 * Read a Keystore.
	 * 
	 * @param Sting (path to keystore)
	 * @return KeyStore
	 * 
	 */
	static private KeyStore readBKS(String pathToBKS) {
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("BKS");
			File bks  = new File(pathToBKS);
			FileInputStream in = new FileInputStream(bks);
			ks.load(in, "ironcontrol".toCharArray());
			in.close();
			logger.debug("successful load tke keystore: " + pathToBKS);
		} catch (KeyStoreException e) {
			logger.error(e.getMessage(), e);
			//throw new KeyStoreException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			//throw new FileNotFoundException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			//throw new NoSuchAlgorithmException(e.getMessage(), e);
		} catch (CertificateException e) {
			logger.error(e.getMessage(), e);
			//throw new CertificateException(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			//throw new IOException(e.getMessage(), e);
		}

		return ks;

	}

	/**
	 * Load a certificate.
	 * 
	 * @param Sting (path to cerificate)
	 * @return Certificate
	 */
	public static Certificate readCertificate(String pathToCertificate){
		CertificateFactory certf = null;
		Certificate cer = null;
		try {
			certf = CertificateFactory.getInstance("X.509");
			File file  = new File(pathToCertificate);
			FileInputStream in = new FileInputStream(file);
			cer = certf.generateCertificate(in);
			in.close();
			logger.debug("successful loaded " + pathToCertificate);
		} catch (CertificateException e) {
			logger.error(e.getMessage(), e);
			//throw new CertificateException(e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			//throw new FileNotFoundException();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			//throw new IOException(e);
		}



		return cer;

	}

	/**
	 * Load a certificate.
	 * 
	 * @param Sting (path to cerificate)
	 *  
	 */
	private static Certificate readCertificatefromRAW(){
		CertificateFactory certf = null;
		Certificate cer = null;
		InputStream in;
		try {
			certf = CertificateFactory.getInstance("X.509");
			in = KeystoreManager.getCertificateFromRaw();
			cer = certf.generateCertificate(in);
			in.close();
			logger.debug("successful load ironcontrol.pem from RAW");
		} catch (CertificateException e) {
			logger.error(e.getMessage(), e);
		}  catch (IOException e) {
			logger.error(e.getMessage(), e);
		}



		return cer;

	}

	/**
	 * Save the Keystore as ironcontrol.pem.
	 * 
	 * @param KeyStore
	 * @param Sting (path to cerificate)
	 * 
	 */
	private static void saveBKS(KeyStore ks, String mPath){
		File fileKS  = new File(mPath);
		if (!fileKS.exists()){
			try {
				fileKS.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		FileOutputStream outKS = null;
		try {
			outKS = new FileOutputStream(fileKS);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		try {
			ks.store(outKS, "ironcontrol".toCharArray());
			outKS.close(); 
			logger.debug("successful saved ironcontrol.bks on SD card");
		} catch (KeyStoreException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (CertificateException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Save the Keystore as ironcontrol.pem.
	 * 
	 * @param KeyStore
	 * @param Sting (path to cerificate)
	 * 
	 */
	private static void copyDefaultToSD() {
		File fileKS  = new File(PATH_TO_DEF_KEYSTORE);
		File fileCert = new File(PATH_TO_DEF_CERT);
		Certificate defCert = null;
		KeyStore defKS = null;
		if (!fileKS.exists()){
			try {
				fileKS.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);;
			}
		}
		if (!fileCert.exists()){
			try {
				fileCert.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);

			}
		}
		FileOutputStream outKS = null;
		FileOutputStream outCert = null;
		defCert = readCertificatefromRAW();
		defKS = readBKSfromRAW();
		try {
			outKS = new FileOutputStream(fileKS);
			outCert = new FileOutputStream(fileCert);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		try {
			defKS.store(outKS, "ironcontrol".toCharArray());
			outCert.write(defCert.getEncoded());
			outKS.close(); 
			outCert.close();
			logger.debug("successful saved ironcontrol.bks and ironcontrol.pem on SD card");
		} catch (KeyStoreException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (CertificateException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}


	/**
	 * Load the deafault Keystore from RAW.
	 * 
	 * @return KeyStore
	 * 
	 */
	private static KeyStore readBKSfromRAW(){
		KeyStore ks = null;
		InputStream in = null;
		try {
			ks = KeyStore.getInstance("BKS");
			in = KeystoreManager.getKeystoreFromRaw();
			ks.load(in, "ironcontrol".toCharArray());
			logger.debug("Load the default Keystore from RAW!");
			in.close();
		} catch (KeyStoreException e) {
			logger.error(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (CertificateException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return ks;

	}

	/**
	 * Add a certificate to a keystore. 
	 * 
	 */
	public static KeyStore addCerificateToBKS(String pathToCerificate, KeyStore kstore) {
		KeyStore ks = kstore;
		String alias = new File(pathToCerificate).getName();
		Certificate cert = readCertificate(pathToCerificate);
		try {
			if(!ks.isCertificateEntry(alias)){
				try {
					ks.setCertificateEntry(alias, cert);
					logger.debug("The certificate <" + alias + "> was successful added!");
				} catch (KeyStoreException e) {
					logger.error(e.getMessage(), e);
					//throw new KeyStoreException(e.getMessage());
				}
			}else{
				logger.debug("The certificate <" + alias + "> already exists! ");
			}
		} catch (KeyStoreException e) {
			logger.error(e.getMessage(), e);
		}
		return ks;
	}

	/**
	 * Add all X509 certificates from SD card to deafault keystore.
	 * 
	 */
	public static void addAllCerificateToBKS() {
		File certStore = new File(PATH_CERT_DIR);
		String[] list = certStore.list();
		KeyStore ks = null;
		if(list.length > 0){
			ks = readBKS(getPATH_TO_KEYSTORE());
			for (String file : list) {
				try {
					if(!ks.isCertificateEntry(file)){
						ks = KeystoreManager.addCerificateToBKS(PATH_CERT_DIR + "/" + file, ks);
						logger.debug("<"+file + "> successfully added");
					}
					logger.debug("<"+file + "> already included");
				} catch (KeyStoreException e) {
					logger.error(e.getMessage(), e);
				}

			}
			saveBKS(ks, PATH_TO_DEF_KEYSTORE);
		}

	}

	/**
	 * Get a path to the keystore on sd card
	 * 
	 * @return Sting (path)
	 */
	public static String getPATH_TO_KEYSTORE(){
		return KeystoreManager.PATH_TO_DEF_KEYSTORE;
	}

	/**
	 * Get the keystore from RAW as inputstream
	 * 
	 * @return Sting (path)
	 */

	public static InputStream getKeystoreFromRaw(){
		InputStream inputStream = MainActivity.getContext().getResources().openRawResource(R.raw.ironcontrol_keystore);
		return inputStream;
	}

	/**
	 * Get the certificate from RAW as inputstream
	 * 
	 * @return Sting (path)
	 */

	public static InputStream getCertificateFromRaw(){
		InputStream inputStream = MainActivity.getContext().getResources().openRawResource(R.raw.ironcontrol_pem);
		return inputStream;
	}

	/**
	 * Check if a sd-card mounted
	 * 
	 * @return booolean
	 */
	public static boolean isSDMounted(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

	}

}
