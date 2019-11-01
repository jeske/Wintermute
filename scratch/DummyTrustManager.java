/*
 * Wintermute - Personal Data Organizer
 *
 * Copyright (C) 2002, by David Jeske and Neotonic Software Corporation.
 *
 * Written by David Jeske <jeske@neotonic.com>.
 *
 * CONFIDENTIAL : This is confidential internal source code. Redistribution
 *   is strictly forbidden.
 */

/*
 * DummyTrustManager.java
 *
 * Created on October 28, 2002, 11:37 AM
 */

package packrat;

/**
 *
 * @author  David Jeske
 */
       import com.sun.net.ssl.X509TrustManager;
    import java.security.cert.X509Certificate;

   public class DummyTrustManager implements X509TrustManager {
 
    public boolean isClientTrusted( X509Certificate[] cert) {
      return true;
    }

    public boolean isServerTrusted( X509Certificate[] cert) {
      return true;
    }

    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[ 0];
    }
  }
