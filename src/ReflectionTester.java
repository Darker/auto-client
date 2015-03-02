
import automat_settings.Input;
import automat_settings.InputHandlers;
import automat_settings.input_handlers.*;
import javax.swing.*;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jakub
 */
public class ReflectionTester {
   public static void main(String[] args) throws Exception
   {
     InputHandlers.register(InputJTextField.class,  JTextField.class);
     InputHandlers.register(InputJCheckBox.class,  JCheckBox.class);
     
     JCheckBox in = new JCheckBox();
     in.setSelected(true);
     Input test;
     try {
     test = InputHandlers.fromJComponent(in, 
         null
         , null);
     }
     catch(NoSuchMethodException e) {
       System.out.println(e.getMessage());
       throw e;
     }
     System.out.println(test.getValue());
   }
}
