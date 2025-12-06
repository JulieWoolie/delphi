package com.juliewoolie.dom.event;

import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.Node;
import org.jetbrains.annotations.Nullable;

/**
 * An object which can host event listeners and be the target of {@link Event} dispatches
 */
public interface EventTarget {

  /**
   * Adds an event listener to the specified type.
   * @param eventType Event type
   * @param listener Event listener
   *
   * @throws NullPointerException if either {@code eventType} or {@code listener} is {@code null}
   */
  void addEventListener(String eventType, EventListener listener);

  /**
   * Removes an event listener.
   * <p>
   * If either the {@code eventType} or {@code listener} is {@code null}, this
   * return false.
   *
   * @param eventType Event type
   * @param listener Event listener
   *
   * @return {@code true}, if the listener was registered for the specified {@code eventType} and
   *         was removed, {@code false} otherwise.
   *
   * @throws NullPointerException if either {@code eventType} or {@code listener} is {@code null}
   */
  boolean removeEventListener(String eventType, EventListener listener);

  /**
   * Dispatches an event.
   * <p>
   * Events are executed first by the target they are dispatched on. Then, if the event
   * is set to bubble (with {@link Event#isBubbling()}) then it will bubble up through
   * the document tree.
   * <br>
   * Finally, the event will be dispatched to {@link Document#getGlobalTarget()}, unless the
   * event has been cancelled.
   *
   * @param event Event to dispatch
   *
   * @throws NullPointerException if {@code event} is {@code null}
   */
  void dispatchEvent(Event event);

  /**
   * Set the click listener. The set listener will only be called when a
   * player left-clicks the target.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#CLICK} events.
   *
   * @param listener Click listener
   */
  void onClick(@Nullable EventListener.Typed<MouseEvent> listener);

  /**
   * Get the left-click listener
   * @return Click listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MouseEvent> getOnClick();

  /**
   * Set the right-click listener.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#CLICK} events.
   *
   * @param listener Right click listener
   */
  void onRightClick(@Nullable EventListener.Typed<MouseEvent> listener);

  /**
   * Get the right-click listener
   * @return Click listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MouseEvent> getOnRightClick();

  /**
   * Set the mouse enter listener. The listener is called when a player's
   * cursor enters the target.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MOUSE_ENTER} events.
   *
   * @param listener Mouse enter listener
   */
  void onMouseEnter(@Nullable EventListener.Typed<MouseEvent> listener);

  /**
   * Get the mouse enter listener.
   * @return Mouse enter listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MouseEvent> getOnMouseEnter();

  /**
   * Set the mouse exit listener. The listener is called when a player's
   * cursor exits the target.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MOUSE_LEAVE} events.
   *
   * @param listener Mouse exit listener
   */
  void onMouseExit(@Nullable EventListener.Typed<MouseEvent> listener);

  /**
   * Get the mouse exit listener.
   * @return Mouse exit listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MouseEvent> getOnMouseExit();

  /**
   * Set the mouse move listener. The listener is only called when a player's
   * cursor moves inside the target. If a player's cursor exits or enters the
   * target, the listener is not called.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MOUSE_MOVE} events.
   *
   * @param listener Mouse move listener
   */
  void onMouseMove(@Nullable EventListener.Typed<MouseEvent> listener);

  /**
   * Get the mouse move listener.
   * @return Mouse move listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MouseEvent> getOnMouseMove();

  /**
   * Set the append listener. The listener is called when a child {@link Node}
   * is appended to the target.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#APPEND_CHILD} events.
   *
   * @param listener Child append listener
   */
  void onAppendChild(@Nullable EventListener.Typed<MutationEvent> listener);

  /**
   * Get the append listener.
   * @return append listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MutationEvent> getOnAppendChild();

  /**
   * Set the remove child listener. The listener is called when a child
   * {@link Node} is removed from the target.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#REMOVE_CHILD} events.
   *
   * @param listener Child remove listener
   */
  void onRemoveChild(@Nullable EventListener.Typed<MutationEvent> listener);

  /**
   * Get the remove child listener.
   * @return remove child listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<MutationEvent> getOnRemoveChild();

  /**
   * Set the attribute change listener. The listener is called whenever any
   * change is made to an attribute's value.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_ATTR} events.
   *
   * @param listener Attribute listener
   */
  void onAttributeChange(@Nullable EventListener.Typed<AttributeMutateEvent> listener);
  
  /**
   * Get the attribute change listener.
   * @return attribute change listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnAttributeChange();
  
  /**
   * Set the attribute set listener. The listener is called whenever an
   * existing attribute's value is set.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_ATTR} events.
   *
   * @param listener Attribute set listener
   */
  void onSetAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the attribute set listener.
   * @return attribute set listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnSetAttribute();

  /**
   * Set the attribute removal listener. The listener is called whenever an
   * existing attribute's value is removed.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_ATTR} events.
   *
   * @param listener
   */
  void onRemoveAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the attribute remove listener.
   * @return attribute remove listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnRemoveAttribute();

  /**
   * Set the attribute addition listener. The listener is called whenever a
   * new attribute is set.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_ATTR} events.
   *
   * @param listener Attribute addition listener
   */
  void onAddAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the attribute addition listener.
   * @return attribute addition listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnAddAttribute();

  /**
   * Set the option change listener. The listener is called whenever any
   * change is made to an document option's value.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_OPTION} events.
   *
   * @param listener Option listener
   */
  void onOptionChange(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the option change listener.
   * @return option change listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnOptionChange();

  /**
   * Set the option set listener. The listener is called whenever an existing
   * document option's value is set.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_OPTION} events.
   *
   * @param listener Option set listener
   */
  void onSetOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the option set listener.
   * @return option set listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnSetOption();

  /**
   * Set the option removal listener. The listener is called whenever an
   * existing document option's value is removed.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_OPTION} events.
   *
   * @param listener Option remove listener
   */
  void onRemoveOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the option removal listener.
   * @return option removal listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnRemoveOption();

  /**
   * Set the option addition listener. The listener is called whenever a
   * new document option is set.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#MODIFY_OPTION} events.
   *
   * @param listener Option addition listener
   */
  void onAddOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener);

  /**
   * Get the option addition listener.
   * @return option addition listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<AttributeMutateEvent> getOnAddOption();

  /**
   * Set the input listener. The listener is called whenever the input of an
   * {@link InputElement} is changed, either by player or
   * by calling the {@link InputElement#setValue(String)}
   * method.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#INPUT} events.
   *
   * @param listener Input listener
   */
  void onInput(@Nullable EventListener.Typed<InputEvent> listener);

  /**
   * Get the input listener.
   * @return input listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<InputEvent> getOnInput();

  /**
   * Set the DOM loaded listener. The listener is called when the DOM tree is
   * finished loading.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#DOM_LOADED} events.
   *
   * @param listener DOM loaded listener
   */
  void onLoaded(@Nullable EventListener listener);

  /**
   * Get the DOM loaded listener.
   * @return DOM loaded listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<InputEvent> getOnLoaded();

  /**
   * Set the DOM spawned listener. The listener is called when the document
   * view is spawned and becomes visible to players.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#DOM_SPAWNED} events.
   *
   * @param listener Spawn listener
   */
  void onSpawned(@Nullable EventListener listener);

  /**
   * Get the DOM spawned listener.
   * @return DOM spawned listener, or {@code null}, if not set
   */
  @Nullable EventListener getOnSpawned();

  /**
   * Set the DOM closing listener. The listener is called when the document
   * view starts closing.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling
   * this method again
   * <p>
   * Listens to {@link EventTypes#DOM_CLOSING} events.
   *
   * @param listener Closing listener
   */
  void onClosing(@Nullable EventListener listener);

  /**
   * Get the DOM closing listener.
   * @return DOM closing listener, or {@code null}, if not set
   */
  @Nullable EventListener getOnClosing();

  /**
   * Set the text content change listener. The listener is called when a text node is modified.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling this method again.
   *
   * @param listener Content change listener
   */
  void onContentChanged(@Nullable EventListener listener);

  /**
   * Get the text content change listener.
   * @return text content change listener, or {@code null}, if not set
   */
  @Nullable EventListener getOnContentChanged();

  /**
   * Set the view move listener. The listener is called when the
   * {@link com.juliewoolie.delphi.DocumentView} is moved.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling this method again.
   *
   * @param listener View move listener
   */
  void onViewMoved(@Nullable EventListener listener);

  /**
   * Get the view move listener.
   * @return View move listener, or {@code null}, if not set
   */
  @Nullable EventListener getOnViewMoved();

  /**
   * Set the tooltip change listener. The listener is called when an element's tooltip is changed
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling this method again.
   *
   * @param listener Tooltip change listener
   */
  void onTooltipChanged(@Nullable EventListener.Typed<TooltipEvent> listener);

  /**
   * Get the tooltip change listener.
   * @return Tooltip change listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<TooltipEvent> getOnTooltipChanged();

  /**
   * Set the player add listener. The listener is called when a player is added to the
   * {@link DocumentView#getPlayers()} player set.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling this method again.
   *
   * @param listener Content change listener
   */
  void onPlayerAdded(@Nullable EventListener.Typed<PlayerSetEvent> listener);

  /**
   * Get the player add listener.
   * @return Player add listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<PlayerSetEvent> getOnPlayerAdded();

  /**
   * Set the player removal listener. The listener is called when a player is removed from the
   * {@link DocumentView#getPlayers()} player set.
   * <p>
   * Can be removed by setting this to {@code null} and overridden by calling this method again.
   *
   * @param listener Content change listener
   */
  void onPlayerRemoved(@Nullable EventListener.Typed<PlayerSetEvent> listener);

  /**
   * Get the player removal listener.
   * @return Player removal listener, or {@code null}, if not set
   */
  @Nullable EventListener.Typed<PlayerSetEvent> getOnPlayerRemoved();
}
