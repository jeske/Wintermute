/*
 * Wintermute - Personal Data Organizer
 * 
 * Copyright (C) 2002, by David Jeske
 *
 * Written by David Jeske <jeske@neotonic.com>. 
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

package simpleimap;

import com.sun.net.ssl.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;


/**
 * DummySSLSocketFactory
 *
 * @author Eugen Kuleshov
 */
public class DummySSLSocketFactory extends SSLSocketFactory {
  private SSLSocketFactory factory;

  public DummySSLSocketFactory() {
    Debug.debug( "DummySocketFactory instantiated");
    try {
      SSLContext sslcontext = SSLContext.getInstance( "TLS");
      sslcontext.init( null,
                       // new KeyManager[] { new DummyKeyManager()},
                       new TrustManager[] { new DummyTrustManager()},
                       new java.security.SecureRandom());
      factory = ( SSLSocketFactory) sslcontext.getSocketFactory();

    } catch( Exception ex) {
      ex.printStackTrace();
    }
  }

  public static SocketFactory getDefault() {
    Debug.debug( "DummySocketFactory.getDefault()");
    return new DummySSLSocketFactory();
  }

  public Socket createSocket( Socket socket, String s, int i, boolean flag)
      throws IOException {
    Debug.debug( "DummySocketFactory.createSocket()");
    return factory.createSocket( socket, s, i, flag);
  }

  public Socket createSocket( InetAddress inaddr, int i,
                              InetAddress inaddr1, int j) throws IOException {
    Debug.debug( "DummySocketFactory.createSocket()");
    return factory.createSocket( inaddr, i, inaddr1, j);
  }

  public Socket createSocket( InetAddress inaddr, int i)
      throws IOException {
    Debug.debug( "DummySocketFactory.createSocket()");
    return factory.createSocket( inaddr, i);
  }

  public Socket createSocket( String s, int i, InetAddress inaddr, int j)
      throws IOException {
    Debug.debug( "DummySocketFactory.createSocket()");
    return factory.createSocket( s, i, inaddr, j);
  }

  public Socket createSocket( String s, int i) throws IOException {
    Debug.debug( "DummySocketFactory.createSocket()");
    return factory.createSocket( s, i);
  }

  public String[] getDefaultCipherSuites() {
    Debug.debug( "DummySocketFactory.getDefaultCipherSuites()");
    return factory.getSupportedCipherSuites();
  }

  public String[] getSupportedCipherSuites() {
    Debug.debug( "DummySocketFactory.getSupportedCipherSuites()");
    return factory.getSupportedCipherSuites();
  }
}

