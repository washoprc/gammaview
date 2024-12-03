/*
    GAMMA Viewer - CacheSettingPanel : Panel for cache settings
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


    GAMMA Viewer - CacheSettingPanel : ����å��������ѥѥͥ�
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


package gammaviewer.UI;

import gammaviewer.ViewerConstants;
import gammaviewer.InfomationException;
import gammaviewer.Data.ViewerDatas;
import gammaviewer.Data.ViewerMode;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class CacheSettingPanel extends JPanel implements ActionListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Parent dialog
  ControlDialog ParentDialog = null;

  // Panel
  JPanel CachePanel  = new JPanel();
  JPanel ButtonPanel = new JPanel();

  // Button
  JButton ChangeButton = new JButton( "�ѹ�" );

  // Layout
  GridBagLayout RootLayout  = new GridBagLayout();
  GridLayout CacheLayout    = new GridLayout( 0, 1, 2, 2 );

  // Labels
  JLabel CachePathLabel        = new JLabel( "����å���ѥ�" );
  JLabel CachePathInfoLabel    = new JLabel( "����å��夵�줿�ƥ����ȥե�����Ϥ��Υǥ��쥯�ȥ����¸����ޤ���" );
  JLabel MaxCacheSizeLabel     = new JLabel( "���祭��å���ե������" );
  JLabel MaxCacheSizeInfoLabel = new JLabel( "����å��夹��ե�����κ���ο������ꤷ�ޤ���\uFF0D1��̵���¤Ǥ���" );

  // Check boxs
  JCheckBox AutoCacheBox = new JCheckBox( "AutoUpdate�ΤȤ��˥���å��夹��", false );
  JCheckBox ShotCacheBox = new JCheckBox( "����å�����⡼�ɤΤȤ��˥���å��夹��", true );

  // Text feilds
  JTextField CachePathField      = new JTextField( 100 );
  JTextField MaxCacheFileField   = new JTextField( 10 );


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  public CacheSettingPanel( ControlDialog autoDialog )
  {
    try
    {
      // Set window manager
      ParentDialog = autoDialog;

      // GridBag layout
      CachePanel.setLayout( CacheLayout );

      // Cache panel arround setting
      CachePanel.add( CachePathLabel );
      CachePanel.add( CachePathInfoLabel );
      CachePanel.add( CachePathField );
      CachePanel.add( MaxCacheSizeLabel );
      CachePanel.add( MaxCacheSizeInfoLabel );
      CachePanel.add( MaxCacheFileField );
      CachePanel.add( AutoCacheBox );
      CachePanel.add( ShotCacheBox );

      // Button and button panel
      ButtonPanel.add( ChangeButton );
      ChangeButton.addActionListener( this );
      ChangeButton.setActionCommand( "CHANGE_CACHE" );

      // Root panel

      GridBagConstraints autoConstraints = new GridBagConstraints();
      autoConstraints.weightx     = 1.0;
      autoConstraints.fill        = GridBagConstraints.BOTH;
      autoConstraints.insets      = new Insets(2, 2, 2, 2);
      autoConstraints.ipadx       = autoConstraints.ipady = 2;
      autoConstraints.gridwidth   = GridBagConstraints.REMAINDER;

      this.setLayout( RootLayout );
      RootLayout.setConstraints( CachePanel, autoConstraints );
      this.add( CachePanel );
      RootLayout.setConstraints( ButtonPanel, autoConstraints );
      this.add( ButtonPanel );

      /* Initialize components */

      this.loadData();
    }
    catch( Exception autoExcept )
    {
      autoExcept.printStackTrace();
    }
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Action performing
  // Argument  : ActionEvent
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    try
    {
      if( e.getActionCommand().equals( "CHANGE_CACHE" ) )
      {
        int autoAnswer =
            JOptionPane.showConfirmDialog( null, "����å���������ѹ����ޤ���������Ǥ�����", "�ѹ�",
                                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );

        if( autoAnswer == JOptionPane.YES_OPTION )
          this.saveData();
      }
    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
    }
    catch( NumberFormatException autoNmbrExcpt )
    {
      Object[] autoMessage = { "�����Ǥ���٤��Ȥ��������Ǥ���ޤ���",
                               "����ץ�󥰥����ࡢ����Ǿ������򸫤ʤ����ƤߤƤ���������" };
      JOptionPane.showMessageDialog( null, autoMessage, "Error", JOptionPane.ERROR_MESSAGE );
    }
    catch( Exception autoExcpt )
    {
      Object[] autoMessage = { "ͽ�����ʤ��㳰��ȯ�����ޤ�����",
                               "�ʲ��ξ������ƥʡ�����𤷤Ƥ���������Ƚ�����ޤ���",
                               "CacheSettingPanel.saveData(), " + autoExcpt.toString() };
      JOptionPane.showMessageDialog( null, autoMessage, "Ϣ��򤪴ꤤ���ޤ���", JOptionPane.ERROR_MESSAGE );
    }
  }


  // Purpose   : Save data
  // Argument  : void
  // Return    : void
  public void saveData() throws InfomationException, NumberFormatException
  {
    ViewerDatas autoData = ParentDialog.getData();

    // Save cache infomations
    autoData.getMode( ViewerConstants.MODE_AUTOUPDATE ).doCache( AutoCacheBox.isSelected() );
    autoData.getMode( ViewerConstants.MODE_CHOOSESHOT ).doCache( ShotCacheBox.isSelected() );
    autoData.setCachePath( CachePathField.getText() );
    autoData.setMaxCacheSize( Integer.parseInt( MaxCacheFileField.getText() ) );
  }


  // Purpose   : Load data
  // Argument  : void
  // Return    : void
  public void loadData()
  {
    ViewerDatas autoData = ParentDialog.getData();

    // Load cache infomations

    // Set cache path
    CachePathField.setText( autoData.getCachePath() );
    // And set max cache file amount
    MaxCacheFileField.setText( String.valueOf( autoData.getMaxCacheFileAmount() ) );
    // Whether to do caching or not
    ViewerMode autoAutoUpdate = autoData.getMode( ViewerConstants.MODE_AUTOUPDATE );
    ViewerMode autoShotChoose = autoData.getMode( ViewerConstants.MODE_CHOOSESHOT );
    AutoCacheBox.setSelected( autoAutoUpdate.doCache() );
    ShotCacheBox.setSelected( autoShotChoose.doCache() );
  }
}