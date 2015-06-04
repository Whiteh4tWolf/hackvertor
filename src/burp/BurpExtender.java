package burp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.unbescape.css.CssEscape;
import org.unbescape.css.CssStringEscapeLevel;
import org.unbescape.css.CssStringEscapeType;
import org.unbescape.html.HtmlEscape;
import org.unbescape.html.HtmlEscapeLevel;
import org.unbescape.html.HtmlEscapeType;
import org.unbescape.javascript.JavaScriptEscape;
import org.unbescape.javascript.JavaScriptEscapeLevel;
import org.unbescape.javascript.JavaScriptEscapeType;


public class BurpExtender implements IBurpExtender, ITab {
	
	private IBurpExtenderCallbacks callbacks;
	private IExtensionHelpers helpers;
	private JPanel panel;
	private JTextArea inputArea;
	private JTextArea outputArea;
	private PrintWriter stderr;
	private Hackvertor hv;
	
	public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
		helpers = callbacks.getHelpers();
		stderr = new PrintWriter(callbacks.getStderr(), true);
		callbacks.setExtensionName("Hackvertor");
		 SwingUtilities.invokeLater(new Runnable() 
	        {
	            @Override
	            public void run()
	            {	   
	            	JTabbedPane tabs = new JTabbedPane();
	            	hv = new Hackvertor();
	            	hv.init();
	            	hv.buildTabs(tabs);
	            	       	
	            	JPanel buttonsPanel = new JPanel(new GridBagLayout());
	            	panel = new JPanel(new GridBagLayout());
	            	
	            	GridBagConstraints c = new GridBagConstraints();	            	
	            	inputArea = new JTextArea();	         
	            	final JLabel inputLabel = new JLabel("Input:");
	            	                          	              	            	
                	DocumentListener documentListener = new DocumentListener() {
            	      public void changedUpdate(DocumentEvent documentEvent) {
            	    	  updateLen(documentEvent);
            	      }
            	      public void insertUpdate(DocumentEvent documentEvent) {
            	    	  updateLen(documentEvent);
            	      }
            	      public void removeUpdate(DocumentEvent documentEvent) {
            	    	  updateLen(documentEvent);
            	      }
            	      private void updateLen(DocumentEvent documentEvent) {
            	    	int len = inputArea.getText().length();
                      	inputLabel.setText("Input:"+len); 
            	      }
            	    };
            	    inputArea.getDocument().addDocumentListener(documentListener);                	
	            	inputArea.addKeyListener(new KeyAdapter() {
	                    @Override
	                    public void keyPressed(KeyEvent e) {	                    
	                        if (e.getKeyCode() == KeyEvent.VK_TAB) {
	                            if (e.getModifiers() > 0) {
	                            	inputArea.transferFocusBackward();
	                            } else {
	                            	inputArea.transferFocus();
	                            }
	                            e.consume();
	                        }
	                    }
	                });
	                inputArea.setPreferredSize(new Dimension(750, 500));	                	              
	                outputArea = new JTextArea();
	                final JLabel outputLabel = new JLabel("Output:");
	                DocumentListener documentListener2 = new DocumentListener() {
            	      public void changedUpdate(DocumentEvent documentEvent) {
            	    	  updateLen(documentEvent);
            	      }
            	      public void insertUpdate(DocumentEvent documentEvent) {
            	    	  updateLen(documentEvent);
            	      }
            	      public void removeUpdate(DocumentEvent documentEvent) {
            	    	  updateLen(documentEvent);
            	      }
            	      private void updateLen(DocumentEvent documentEvent) {
            	    	int len = outputArea.getText().length();
                      	outputLabel.setText("Output:"+len); 
            	      }
            	    };
            	    outputArea.getDocument().addDocumentListener(documentListener2);
	                outputArea.addKeyListener(new KeyAdapter() {
	                    @Override
	                    public void keyPressed(KeyEvent e) {	                    
	                        if (e.getKeyCode() == KeyEvent.VK_TAB) {
	                            if (e.getModifiers() > 0) {
	                            	outputArea.transferFocusBackward();
	                            } else {
	                            	outputArea.transferFocus();
	                            }
	                            e.consume();
	                        }
	                    }
	                });	                
	                outputArea.setPreferredSize(new Dimension(750, 500));
	            	panel.setPreferredSize(new Dimension(800,600));	            	
	            	final JButton swapButton = new JButton("Swap");
	            	swapButton.setBackground(Color.black);
	            	swapButton.setForeground(Color.white);
	            	swapButton.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {
	                	  inputArea.setText(outputArea.getText());
	                	  outputArea.setText("");
	                	  inputArea.requestFocus();
	                  }
	                });
	            	final JButton convertButton = new JButton("Convert");
	                convertButton.setBackground(Color.decode("#005a70"));
	                convertButton.setForeground(Color.white);
	                convertButton.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {	                	
	                	  outputArea.setText(hv.convert(inputArea.getText()));
	                	  outputArea.selectAll();
	                  }
	                });
	                final JButton selectInputButton = new JButton("Select input");
	                selectInputButton.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {	                	 
	                	  inputArea.requestFocus();
	                	  inputArea.selectAll();	                		                	                    
	                  }
	                });
	                selectInputButton.setForeground(Color.white);
	                selectInputButton.setBackground(Color.black);
	                final JButton selectOutputButton = new JButton("Select output");
	                selectOutputButton.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {	                	 
	                	  outputArea.requestFocus();
	                	  outputArea.selectAll();	                		                	                    
	                  }
	                });
	                selectOutputButton.setForeground(Color.white);
	                selectOutputButton.setBackground(Color.black);
	                final JButton clearTagsButton = new JButton("Clear tags");
	                clearTagsButton.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {	                	 	                	 
	                	  String input = inputArea.getText();
	                	  input = input.replaceAll("<@\\w+_\\d+>","");
	                	  input = input.replaceAll("<@/\\w+_\\d+>","");
	                	  inputArea.setText(input);	                	  	                	  
	                	  inputArea.requestFocus();
	                  }
	                });
	                clearTagsButton.setForeground(Color.white);
	                clearTagsButton.setBackground(Color.black);
	                final JButton clearButton = new JButton("Clear");
	                clearButton.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {	                	 	                	 
	                	  inputArea.setText("");	                	  
	                	  outputArea.setText("");
	                	  inputArea.requestFocus();
	                  }
	                });
	                clearButton.setForeground(Color.white);
	                clearButton.setBackground(Color.black);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 0;
	                c.gridy = 0;
	                c.ipady = 0;
	                buttonsPanel.add(clearButton,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 1;
	                c.gridy = 0;
	                c.ipady = 0;
	                buttonsPanel.add(clearTagsButton,c);	              
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 2;
	                c.gridy = 0;
	                c.ipady = 0;
	                buttonsPanel.add(swapButton,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 3;
	                c.gridy = 0;
	                c.ipady = 0;
	                buttonsPanel.add(selectInputButton,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 4;
	                c.gridy = 0;
	                c.ipady = 0;
	                buttonsPanel.add(selectOutputButton,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 5;
	                c.gridy = 0;
	                c.ipady = 0;
	                buttonsPanel.add(convertButton,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 0;
	                c.gridy = 0;
	                c.ipady = 0;
	                c.gridwidth = 4;
	                panel.add(tabs,c);
	              	c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 0;
	                c.gridy = 1;
	                c.ipady = 0;
	                c.gridwidth = 1;
	                panel.add(inputLabel,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 0;
	                c.gridy = 2;	              
	                c.ipady = 100;
	                c.gridwidth = 1;
	                panel.add(inputArea,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 1;
	                c.gridy = 1;
	                c.ipady = 0;
	                c.gridwidth = 1;
	                panel.add(outputLabel,c);
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 1;
	                c.gridy = 2;
	                c.ipady = 100;
	                c.gridwidth = 1;
	                panel.add(outputArea,c);	 
	                c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = 0;
	                c.gridy = 3;
	                c.ipady = 0;	
	                c.gridwidth = 1;
	                panel.add(buttonsPanel,c);	                            
	                callbacks.customizeUiComponent(panel);
	                
	                callbacks.addSuiteTab(BurpExtender.this);	          
	            }
	        });
		
	}
	public String getTabCaption() {
		return "Hackvertor";
	}
	
	public Component getUiComponent()
    {
        return panel;
    }
	class Tag {
		public String category;
		public String name;
		Tag(String tagCategory, String tagName) {
			this.category = tagCategory;
			this.name = tagName;		
		}
	}
	class Hackvertor {	
		private int tagCounter = 0;
		private ArrayList<Tag> tags = new ArrayList<Tag>();
		public void buildTabs(JTabbedPane tabs) {
			tabs.addTab("Encode", createButtons("Encode"));
        	tabs.addTab("Decode", createButtons("Decode"));
        	tabs.addTab("String", createButtons("String"));
        	tabs.addTab("Convert", createButtons("Convert"));
		}
		public void init() {
			tags.add(new Tag("Encode","base64"));
			tags.add(new Tag("Encode","html_entities"));
			tags.add(new Tag("Encode","html5_entities"));
			tags.add(new Tag("Encode","hex_entities"));
			tags.add(new Tag("Encode","hex_escapes"));
			tags.add(new Tag("Encode","octal_escapes"));
			tags.add(new Tag("Encode","dec_entities"));
			tags.add(new Tag("Encode","unicode_escapes"));
			tags.add(new Tag("Encode","css_escapes"));
			tags.add(new Tag("Encode","css_escapes6"));
			tags.add(new Tag("Encode","urlencode"));
			tags.add(new Tag("Decode","decode_base64"));
			tags.add(new Tag("Decode","decode_html_entities"));
			tags.add(new Tag("Decode","decode_html5_entities"));
			tags.add(new Tag("Decode","decode_js_string"));
			tags.add(new Tag("Decode","decode_url"));
			tags.add(new Tag("Decode","decode_css_escapes"));
			tags.add(new Tag("Decode","decode_octal_escapes"));
			tags.add(new Tag("Decode","decode_unicode_escapes"));
			tags.add(new Tag("Convert","dec2hex"));
			tags.add(new Tag("Convert","dec2oct"));
			tags.add(new Tag("Convert","dec2bin"));
			tags.add(new Tag("Convert","hex2dec"));
			tags.add(new Tag("Convert","oct2dec"));
			tags.add(new Tag("Convert","bin2dec"));
			tags.add(new Tag("String","uppercase"));
			tags.add(new Tag("String","lowercase"));
			tags.add(new Tag("String","capitalise"));
			tags.add(new Tag("String","uncapitalise"));
			tags.add(new Tag("String","from_charcode"));
		}
		public String html_entities(String str) {
			return StringEscapeUtils.escapeHtml4(str);
		}
		public String decode_html_entities(String str) {
			return StringEscapeUtils.unescapeHtml4(str);
		}
		public String base64Encode(String str) {
			return helpers.base64Encode(str);
		}
		public String decode_base64(String str) {
			try{
				str = helpers.bytesToString(helpers.base64Decode(str));
			} catch(Exception e){ 
				stderr.println(e.getMessage());
			}
			return str;
		}
		public String urlencode(String str) {
			try {
	            str = URLEncoder.encode(str, "UTF-8");		          
	        } catch (UnsupportedEncodingException e) {
	        	stderr.println(e.getMessage());
	        }
			return str;
		}
		public String decode_url(String str) {
			try {
	            str = URLDecoder.decode(str, "UTF-8");		          
	        } catch (UnsupportedEncodingException e) {
	        	stderr.println(e.getMessage());
	        }
			return str;
		}
		public String uppercase(String str) {
			return StringUtils.upperCase(str);
		}
		public String lowercase(String str) {
			return StringUtils.lowerCase(str);
		}
		public String capitalise(String str) {
			return StringUtils.capitalize(str);
		}
		public String uncapitalise(String str) {
			return StringUtils.uncapitalize(str);
		}
		public String html5_entities(String str) {
			return HtmlEscape.escapeHtml(str, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_3_ALL_NON_ALPHANUMERIC);
		}
		public String decode_html5_entities(String str) {
			return HtmlEscape.unescapeHtml(str);
		}
		public String hex_entities(String str) {
			return HtmlEscape.escapeHtml(str, HtmlEscapeType.HEXADECIMAL_REFERENCES,HtmlEscapeLevel.LEVEL_4_ALL_CHARACTERS);
		}
		public String dec_entities(String str) {
			return HtmlEscape.escapeHtml(str, HtmlEscapeType.DECIMAL_REFERENCES,HtmlEscapeLevel.LEVEL_4_ALL_CHARACTERS);
		}
		public String hex_escapes(String str) {
			return JavaScriptEscape.escapeJavaScript(str,JavaScriptEscapeType.XHEXA_DEFAULT_TO_UHEXA, JavaScriptEscapeLevel.LEVEL_4_ALL_CHARACTERS);
		}
		public String octal_escapes(String str) {
			StringBuilder converted = new StringBuilder();
			for(int i=0;i<str.length();i++) {
				converted.append("\\" + Integer.toOctalString(Character.codePointAt(str, i)));
			}
			return converted.toString();
		}
		public String decode_octal_escapes(String str) {
			return this.decode_js_string(str);
		}
		public String css_escapes(String str) {
			return CssEscape.escapeCssString(str,CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_4_ALL_CHARACTERS);
		}
		public String css_escapes6(String str) {
			return CssEscape.escapeCssString(str,CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_SIX_DIGIT_HEXA, CssStringEscapeLevel.LEVEL_4_ALL_CHARACTERS);
		}
		public String unicode_escapes(String str) {
			return JavaScriptEscape.escapeJavaScript(str,JavaScriptEscapeType.UHEXA, JavaScriptEscapeLevel.LEVEL_4_ALL_CHARACTERS);
		}
		public String decode_js_string(String str) {
			return JavaScriptEscape.unescapeJavaScript(str);
		}
		public String decode_css_escapes(String str) {
			return CssEscape.unescapeCss(str);
		}
		public String dec2hex(String str) {
			try{
				str = Integer.toHexString(Integer.parseInt(str));
			} catch(NumberFormatException e){ 
				stderr.println(e.getMessage()); 
			}
			return str;
		}
		public String dec2oct(String str) {
			try{
				str = Integer.toOctalString(Integer.parseInt(str));
			} catch(NumberFormatException e){ 
				stderr.println(e.getMessage()); 
			}
			return str;
		}
		public String dec2bin(String str) {
			try{
				str = Integer.toBinaryString(Integer.parseInt(str));
			} catch(NumberFormatException e){ 
				stderr.println(e.getMessage()); 
			}
			return str;
		}
		public String hex2dec(String str) {
			try{
				str = Integer.toString(Integer.parseInt(str,16));
			} catch(NumberFormatException e){ 
				stderr.println(e.getMessage()); 
			}
			return str;
		}
		public String oct2dec(String str) {
			try{
				str = Integer.toString(Integer.parseInt(str,8));
			} catch(NumberFormatException e){ 
				stderr.println(e.getMessage()); 
			}
			return str;
		}
		public String bin2dec(String str) {
			try{
				str = Integer.toString(Integer.parseInt(str,2));
			} catch(NumberFormatException e){ 
				stderr.println(e.getMessage()); 
			}
			return str;
		}
		public String from_charcode(String str) {
		   String[] chars = str.split(",");
		   String output = "";
		   if(str.length() == 1) {
			   try {
				   output = Character.toString((char) Integer.parseInt(str));
			   } catch(NumberFormatException e){ 
					stderr.println(e.getMessage()); 
			   }
			   return output;
		   } else {
			   for(int i=0;i<chars.length;i++) {
				   try {
					   output += Character.toString((char) Integer.parseInt(chars[i]));
				   } catch(NumberFormatException e){ 
						stderr.println(e.getMessage()); 
				   }
			   }
			   return output;
		   }
		}
		private String callTag(String tag, String output) {
			if(tag.equals("html_entities")) {
				output = this.html_entities(output);
			} else if(tag.equals("decode_html_entities")) {
				output = this.decode_html_entities(output);
			} else if(tag.equals("html5_entities")) {
				output = this.html5_entities(output);
			} else if(tag.equals("hex_entities")) {
				output = this.hex_entities(output);
			} else if(tag.equals("hex_escapes")) {
				output = this.hex_escapes(output);
			} else if(tag.equals("octal_escapes")) {
				output = this.octal_escapes(output);
			} else if(tag.equals("decode_octal_escapes")) {
				output = this.decode_octal_escapes(output);	
			} else if(tag.equals("css_escapes")) {
				output = this.css_escapes(output);
			} else if(tag.equals("css_escapes6")) {
				output = this.css_escapes6(output);
			} else if(tag.equals("dec_entities")) {
				output = this.dec_entities(output);
			} else if(tag.equals("unicode_escapes")) {
				output = this.unicode_escapes(output);
			} else if(tag.equals("decode_unicode_escapes")) {
				output = this.decode_js_string(output);
			} else if(tag.equals("decode_js_string")) {
				output = this.decode_js_string(output);	
			} else if(tag.equals("decode_html5_entities")) {
				output = this.decode_html5_entities(output);	
			} else if(tag.equals("base64")) {
				output = this.base64Encode(output);
			} else if(tag.equals("decode_base64")) {
				output = this.decode_base64(output);
			} else if(tag.equals("urlencode")) {
				output = this.urlencode(output);
			} else if(tag.equals("decode_url")) {
				output = this.decode_url(output);
			} else if(tag.equals("decode_css_escapes")) {
				output = this.decode_css_escapes(output);	
			} else if(tag.equals("uppercase")) {
				output = this.uppercase(output);
			} else if(tag.equals("lowercase")) {
				output = this.lowercase(output);
			} else if(tag.equals("capitalise")) {
				output = this.capitalise(output);
			} else if(tag.equals("uncapitalise")) {
				output = this.uncapitalise(output);
			} else if(tag.equals("from_charcode")) {
				output = this.from_charcode(output);	
			} else if(tag.equals("dec2hex")) {
				output = this.dec2hex(output);
			} else if(tag.equals("dec2oct")) {
				output = this.dec2oct(output);
			} else if(tag.equals("dec2bin")) {
				output = this.dec2bin(output);
			} else if(tag.equals("hex2dec")) {
				output = this.hex2dec(output);
			} else if(tag.equals("oct2dec")) {
				output = this.oct2dec(output);
			} else if(tag.equals("bin2dec")) {
				output = this.bin2dec(output);
			}
			return output;
		}
		public String convert(String input) {
			String output = input;
			List<String> allMatches = new ArrayList<String>();
			 Matcher m = Pattern.compile("<@/([\\w\\d]+_\\d+)>").matcher(input);			 
			 while (m.find()) {
			   allMatches.add(m.group(1));
			 }
			 for(String tagNameWithID:allMatches) {
				 String code = "";
				 String tagName = tagNameWithID.replaceAll("_\\d+$","");
				 m = Pattern.compile("<@"+tagNameWithID+">([\\d\\D]*?)<@/"+tagNameWithID+">").matcher(output);
				 if(m.find()) {
					code = m.group(1); 
				 } 	
				 String result = this.callTag(tagName,code);
				 output = output.replaceAll("<@"+tagNameWithID+">[\\d\\D]*?<@/"+tagNameWithID+">", result.replace("\\","\\\\").replace("$","\\$"));
			 }
			return output;			
		}
		private JPanel createButtons(String category) {
			panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			int i = 0;
			for(Tag tagObj:tags) {
				if(category == tagObj.category) {
					c.fill = GridBagConstraints.HORIZONTAL;
	                c.weightx = 0.5;
	                c.gridx = i;
	                c.gridy = 0;
	                c.ipady = 0;	
	                c.gridwidth = 1;
	                final JButton btn = new JButton(tagObj.name);
	                btn.setBackground(Color.decode("#005a70"));	            
	            	btn.setForeground(Color.white);
	            	btn.addActionListener(new ActionListener() {
	                  public void actionPerformed(ActionEvent e) {
	                	  String selectedText = inputArea.getSelectedText();
	                	  if(selectedText == null) {
	                		  selectedText = inputArea.getText();
	                	  }
	                	  String tagStart = "<@"+btn.getText()+"_"+tagCounter+">";
	                	  String tagEnd = "<@/"+btn.getText()+"_"+tagCounter+">";	                	 
	                	  inputArea.replaceSelection(tagStart+selectedText+tagEnd);
	                	  tagCounter++;	 
	                	  outputArea.setText(hv.convert(inputArea.getText()));
	                	  outputArea.selectAll();
	                  }
	                });
					panel.add(btn,c);
					i++;
				}
			}
			return panel;
		}
	}
	
}