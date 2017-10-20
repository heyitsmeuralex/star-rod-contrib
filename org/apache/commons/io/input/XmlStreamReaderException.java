package org.apache.commons.io.input;

import java.io.IOException;













































public class XmlStreamReaderException
  extends IOException
{
  private static final long serialVersionUID = 1L;
  private final String bomEncoding;
  private final String xmlGuessEncoding;
  private final String xmlEncoding;
  private final String contentTypeMime;
  private final String contentTypeEncoding;
  
  public XmlStreamReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc)
  {
    this(msg, null, null, bomEnc, xmlGuessEnc, xmlEnc);
  }
  













  public XmlStreamReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc)
  {
    super(msg);
    contentTypeMime = ctMime;
    contentTypeEncoding = ctEnc;
    bomEncoding = bomEnc;
    xmlGuessEncoding = xmlGuessEnc;
    xmlEncoding = xmlEnc;
  }
  




  public String getBomEncoding()
  {
    return bomEncoding;
  }
  




  public String getXmlGuessEncoding()
  {
    return xmlGuessEncoding;
  }
  




  public String getXmlEncoding()
  {
    return xmlEncoding;
  }
  






  public String getContentTypeMime()
  {
    return contentTypeMime;
  }
  







  public String getContentTypeEncoding()
  {
    return contentTypeEncoding;
  }
}
