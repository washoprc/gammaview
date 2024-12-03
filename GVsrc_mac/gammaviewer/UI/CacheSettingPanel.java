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


    GAMMA Viewer - CacheSettingPanel : キャッシュ設定用パネル
    Copyright (C) 2002-2003  津田英介


    このプログラムはフリーソフトウェアです。あなたはこれを、フリーソフ
    トウェア財団によって発行された GNU 一般公衆利用許諾契約書(バージョ
    ン2か、希望によってはそれ以降のバージョンのうちどれか)の定める条件
    の下で再頒布または改変することができます。

    このプログラムは有用であることを願って頒布されますが、*全くの無保
    証* です。商業可能性の保証や特定の目的への適合性は、言外に示された
    ものも含め全く存在しません。詳しくはGNU 一般公衆利用許諾契約書をご
    覧ください。

    あなたはこのプログラムと共に、GNU 一般公衆利用許諾契約書の複製物を
    一部受け取ったはずです。もし受け取っていなければ、フリーソフトウェ
    ア財団まで請求してください(宛先は the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)。
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
  JButton ChangeButton = new JButton( "変更" );

  // Layout
  GridBagLayout RootLayout  = new GridBagLayout();
  GridLayout CacheLayout    = new GridLayout( 0, 1, 2, 2 );

  // Labels
  JLabel CachePathLabel        = new JLabel( "キャッシュパス" );
  JLabel CachePathInfoLabel    = new JLabel( "キャッシュされたテキストファイルはこのディレクトリに保存されます。" );
  JLabel MaxCacheSizeLabel     = new JLabel( "最大キャッシュファイル数" );
  JLabel MaxCacheSizeInfoLabel = new JLabel( "キャッシュするファイルの最大の数を設定します。\uFF0D1で無制限です。" );

  // Check boxs
  JCheckBox AutoCacheBox = new JCheckBox( "AutoUpdateのときにキャッシュする", false );
  JCheckBox ShotCacheBox = new JCheckBox( "ショット選択モードのときにキャッシュする", true );

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
            JOptionPane.showConfirmDialog( null, "キャッシュ設定を変更します。よろしいですか？", "変更",
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
      Object[] autoMessage = { "数字であるべきところが数字でありません。",
                               "サンプリングタイム、最大最小値等を見なおしてみてください。" };
      JOptionPane.showMessageDialog( null, autoMessage, "Error", JOptionPane.ERROR_MESSAGE );
    }
    catch( Exception autoExcpt )
    {
      Object[] autoMessage = { "予期しない例外が発生しました。",
                               "以下の情報をメンテナーに報告していただけると助かります。",
                               "CacheSettingPanel.saveData(), " + autoExcpt.toString() };
      JOptionPane.showMessageDialog( null, autoMessage, "連絡をお願いします。", JOptionPane.ERROR_MESSAGE );
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