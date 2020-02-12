package ant.restaurant;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 * HostnameVerifier
 * bug fixed : <http://iteches.com/archives/45015>
 * @author will_awoke
 * @version 2014-8-15
 * @see CustomizedHostnameVerifier
 * @since
 */
public class CustomizedHostnameVerifier implements HostnameVerifier
{

    @Override
    public boolean verify(String arg0, SSLSession arg1)
    {
        return true;
    }

}