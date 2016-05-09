/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.autoclient.event;

/**
 *
 * @author Jakub
 * @param <T_P> Single argument
 */
public interface CallbackInterface<T_P> extends AnyCallbackInterface {
  void event(T_P param);
}
