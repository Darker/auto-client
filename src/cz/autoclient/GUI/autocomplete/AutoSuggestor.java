/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.GUI.autocomplete;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author David Kroukamp
 * @url http://stackoverflow.com/a/14194104/607407
 */
class AutoSuggestor {

    private final JTextField textField;
    private final Window container;
    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private String typedWord;
    private final ArrayList<String> dictionary = new ArrayList<>();
    private int currentIndexOfSpace, tW, tH;
    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }
    };
    private final Color suggestionsTextColor;
    private final Color suggestionFocusedColor;

    public AutoSuggestor(JTextField textField, Window mainWindow, ArrayList<String> words, Color popUpBackground, Color textColor, Color suggestionFocusedColor, float opacity) {
        this.textField = textField;
        this.suggestionsTextColor = textColor;
        this.container = mainWindow;
        this.suggestionFocusedColor = suggestionFocusedColor;
        this.textField.getDocument().addDocumentListener(documentListener);

        setDictionary(words);

        typedWord = "";
        currentIndexOfSpace = 0;
        tW = 0;
        tH = 0;

        autoSuggestionPopUpWindow = new JWindow(mainWindow);
        autoSuggestionPopUpWindow.setOpacity(opacity);

        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        suggestionsPanel.setBackground(popUpBackground);

        addKeyBindingToRequestFocusInPopUpWindow();
    }

    private void addKeyBindingToRequestFocusInPopUpWindow() {
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        textField.getActionMap().put("Down released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {//focuses the first label on popwindow
                for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
                    if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                        ((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
                        autoSuggestionPopUpWindow.toFront();
                        autoSuggestionPopUpWindow.requestFocusInWindow();
                        suggestionsPanel.requestFocusInWindow();
                        suggestionsPanel.getComponent(i).requestFocusInWindow();
                        break;
                    }
                }
            }
        });
        suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {//allows scrolling of labels in pop window (I know very hacky for now :))

                ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {//more than 1 suggestion
                    for (int i = 0; i < max; i++) {
                        SuggestionLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == max - 1) {
                                lastFocusableIndex = 0;
                                sl.setFocused(false);
                                autoSuggestionPopUpWindow.setVisible(false);
                                setFocusToTextField();
                                checkForAndShowSuggestions();//fire method as if document listener change occured and fired it

                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex <= i) {
                            if (i < max) {
                                sl.setFocused(true);
                                autoSuggestionPopUpWindow.toFront();
                                autoSuggestionPopUpWindow.requestFocusInWindow();
                                suggestionsPanel.requestFocusInWindow();
                                suggestionsPanel.getComponent(i).requestFocusInWindow();
                                lastFocusableIndex = i;
                                break;
                            }
                        }
                    }
                } else {//only a single suggestion was given
                    autoSuggestionPopUpWindow.setVisible(false);
                    setFocusToTextField();
                    checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                }
            }
        });
    }

    private void setFocusToTextField() {
        container.toFront();
        container.requestFocusInWindow();
        textField.requestFocusInWindow();
    }

    public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
        ArrayList<SuggestionLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }

    private void checkForAndShowSuggestions() {
        typedWord = getCurrentlyTypedWord();

        suggestionsPanel.removeAll();//remove previos words/jlabels that were added

        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;

        boolean added = wordTyped(typedWord);

        if (!added) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showPopUpWindow();
            setFocusToTextField();
        }
    }

    protected void addWordToSuggestions(String word) {
        SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);

        calculatePopUpWindowSize(suggestionLabel);

        suggestionsPanel.add(suggestionLabel);
    }

    public String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textField.getText();
        String wordBeingTyped = "";
        if (text.contains(" ")) {
            int tmp = text.lastIndexOf(" ");
            if (tmp >= currentIndexOfSpace) {
                currentIndexOfSpace = tmp;
                wordBeingTyped = text.substring(text.lastIndexOf(" "));
            }
        } else {
            wordBeingTyped = text;
        }
        return wordBeingTyped.trim();
    }

    private void calculatePopUpWindowSize(JLabel label) {
        //so we can size the JWindow correctly
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width;
        }
        tH += label.getPreferredSize().height;
    }

    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
        autoSuggestionPopUpWindow.setSize(tW, tH);
        autoSuggestionPopUpWindow.setVisible(true);

        int windowX = 0;
        int windowY = 0;

        windowX = container.getX() + textField.getX() + 5;
        if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
            windowY = container.getY() + textField.getY() + textField.getHeight() + autoSuggestionPopUpWindow.getMinimumSize().height;
        } else {
            windowY = container.getY() + textField.getY() + textField.getHeight() + autoSuggestionPopUpWindow.getHeight();
        }

        autoSuggestionPopUpWindow.setLocation(windowX, windowY);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
        autoSuggestionPopUpWindow.revalidate();
        autoSuggestionPopUpWindow.repaint();

    }

    public void setDictionary(ArrayList<String> words) {
        dictionary.clear();
        if (words == null) {
            return;//so we can call constructor with null value for dictionary without exception thrown
        }
        for (String word : words) {
            dictionary.add(word);
        }
    }

    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }

    public Window getContainer() {
        return container;
    }

    public JTextField getTextField() {
        return textField;
    }

    public void addToDictionary(String word) {
        dictionary.add(word);
    }

    boolean wordTyped(String typedWord) {

        if (typedWord.isEmpty()) {
            return false;
        }
        //System.out.println("Typed word: " + typedWord);

        boolean suggestionAdded = false;

        for (String word : dictionary) {//get words in the dictionary which we added
            boolean fullymatches = true;
            for (int i = 0; i < typedWord.length(); i++) {//each string in the word
                if (!typedWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {//check for match
                    fullymatches = false;
                    break;
                }
            }
            if (fullymatches) {
                addWordToSuggestions(word);
                suggestionAdded = true;
            }
        }
        return suggestionAdded;
    }
}