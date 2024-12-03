/*
    GAMMA Viewer - LocalHost : Class to read file on local
    Copyright (C) 2002-2003  Tsuda Eisuke


    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


    GAMMA Viewer - LocalHost : ������Υե�������ɤ९�饹
    Copyright (C) 2002-2003  ���ıѲ�


    ���Υץ����ϥե꡼���եȥ������Ǥ������ʤ��Ϥ���򡢥ե꡼����
    �ȥ��������Ĥˤ�ä�ȯ�Ԥ��줿 GNU ���̸������ѵ��������(�С�����
    ��2������˾�ˤ�äƤϤ���ʹߤΥС������Τ����ɤ줫)��������
    �β��Ǻ����ۤޤ��ϲ��Ѥ��뤳�Ȥ��Ǥ��ޤ���

    ���Υץ�����ͭ�ѤǤ��뤳�Ȥ��ä����ۤ���ޤ�����*������̵��
    ��* �Ǥ������Ȳ�ǽ�����ݾڤ��������Ū�ؤ�Ŭ�����ϡ������˼����줿
    ��Τ�ޤ�����¸�ߤ��ޤ��󡣾ܤ�����GNU ���̸������ѵ���������
    ������������

    ���ʤ��Ϥ��Υץ����ȶ��ˡ�GNU ���̸������ѵ���������ʣ��ʪ��
    ����������ä��Ϥ��Ǥ����⤷������äƤ��ʤ���С��ե꡼���եȥ���
    �����Ĥޤ����ᤷ�Ƥ�������(����� the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)��
*/


package gammaviewer.Net;

import gammaviewer.InfomationException;

import java.net.InetAddress;
import java.io.*;


public class LocalHost implements ServerConnection
{
  public LocalHost()    {}

  public void CreateConnection( InetAddress addr, int port ) throws InfomationException {}
  public boolean login(String UserName, String Password) throws InfomationException  { return true; }
  public void logout()  {}


  // Purpose   : Get local file
  // Argument  : Local file path
  // Return    : ByteArray stream
  public ByteArrayOutputStream get( String autoFileName )
                              throws InfomationException,
                                     FileNotFoundException
  {
    try
    {
      ByteArrayOutputStream autoOutputStream = new ByteArrayOutputStream( 9000 );
      BufferedInputStream autoInput = new BufferedInputStream( new FileInputStream( autoFileName ) );

      int i = 0;
      while( ( i = autoInput.read() ) != -1 )
        autoOutputStream.write( i );

      autoInput.close();
      autoOutputStream.close();

      return autoOutputStream;
    }
    catch( FileNotFoundException autoFNFExcpt )
    {
      throw autoFNFExcpt;
    }
    catch( IOException autoIOExcpt )
    {
      throw new InfomationException( "������ե�������ɤ߹�����˥��顼��ȯ�����ޤ������ե�������ɤ�ޤ���" );
    }
  }
}