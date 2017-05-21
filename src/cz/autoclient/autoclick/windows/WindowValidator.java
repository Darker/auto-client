/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.autoclick.windows;

import cz.autoclient.autoclick.exceptions.APIException;
import java.util.regex.Pattern;

/**
 *
 * @author Jakub
 */
public interface WindowValidator {
  public boolean isValid(Window w);
  // comapres two validators
  // useful to cache already validated windows
  public boolean equals(WindowValidator other);
  public static class ExactTitleValidator implements WindowValidator {
    public ExactTitleValidator(String title) {
      this.title = title;
    }
   public final String title;
    @Override
    public boolean isValid(Window w) {
      return title!=null && title.equals(w.getTitle());
    }

    @Override
    public boolean equals(WindowValidator other) {
      if(other instanceof ExactTitleValidator) {
        final String otherTitle = ((ExactTitleValidator)other).title;
        return title==otherTitle || (title!=null && title.equals(otherTitle));
      }
      return false;
    }
  }
  public static class TitlePatternWindowValidator implements WindowValidator {
    protected final Pattern pattern;
    public TitlePatternWindowValidator(Pattern pattern) {
      this.pattern = pattern;
    }
    @Override
    public boolean isValid(Window w) {
      return pattern.matcher(w.getTitle()).matches();
    }

    @Override
    public boolean equals(WindowValidator other) {
      if(other instanceof TitlePatternWindowValidator) {
        final Pattern otherTitle = ((TitlePatternWindowValidator)other).pattern;
        return pattern==otherTitle || (pattern!=null && pattern.equals(otherTitle));
      }
      return false;
    }
  }
  public static class ProcessNameValidator implements WindowValidator {
    public ProcessNameValidator(String processFilename) {
      this.processFilename = processFilename;      
    }
    public final String processFilename;
    @Override
    public boolean isValid(Window w) {
      String filename = "";
      try {
        filename = w.getProcessName();
      }
      catch(APIException e) {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Error getting process handle of window "+w.getTitle());
        return false;
      }
      if(!filename.endsWith(processFilename))
        return false;
      String[] separators = new String[] {"\\", "/"};
      for(int i=0; i<separators.length; ++i) {
        int index = filename.lastIndexOf(separators[i]);
        if(index!=-1) {
          filename = filename.substring(index+1);
        }
      }
      return filename.equals(processFilename);
    }

    @Override
    public boolean equals(WindowValidator other) {
      if(other instanceof ProcessNameValidator) {
        final String otherTitle = ((ProcessNameValidator)other).processFilename;
        return processFilename==otherTitle || (processFilename!=null && processFilename.equals(otherTitle));
      }
      return false;
    }
  }
  /**
   * Requires window to match all validators. Validators are processed in the order
   * in which they are passed to the constructor.
   */
  public static class CompositeValidatorAND implements WindowValidator {
    public CompositeValidatorAND(WindowValidator... validators) {
        this.validators = validators;
    }
    public final WindowValidator[] validators;
    @Override
    public boolean isValid(Window w) {
      for(WindowValidator validator : validators) {
        if(!validator.isValid(w))
          return false;
      }
      return true;
    }

    @Override
    public boolean equals(WindowValidator other) {
      if(other instanceof CompositeValidatorAND) {
        final CompositeValidatorAND otherx = (CompositeValidatorAND)other;
        final WindowValidator[] otherValidators = otherx.validators;
        if(otherValidators.length != validators.length)
          return false;
        for(int i=0; i<otherValidators.length; ++i) {
          if(!validators[i].equals(otherValidators[i])) {
            return false; 
          }
        }
        return true;
      }
      return false;
    }
  }
}
