package net.arcadiusmc.delphidom.event;

import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.InputEvent;
import net.arcadiusmc.dom.event.MouseEvent;
import net.arcadiusmc.dom.event.MutationEvent;
import org.jetbrains.annotations.Nullable;

public interface DelegateTarget extends EventTarget {

  EventTarget getListenerList();

  @Override
  default void addEventListener(String eventType, EventListener listener) {
    getListenerList().addEventListener(eventType, listener);
  }

  @Override
  default boolean removeEventListener(String eventType, EventListener listener) {
    return getListenerList().removeEventListener(eventType, listener);
  }

  @Override
  default void onClick(@Nullable EventListener.Typed<MouseEvent> listener) {
    getListenerList().onClick(listener);
  }

  @Override
  default void onRightClick(@Nullable EventListener.Typed<MouseEvent> listener) {
    getListenerList().onRightClick(listener);
  }

  @Override
  default void onMouseEnter(@Nullable EventListener.Typed<MouseEvent> listener) {
    getListenerList().onMouseEnter(listener);
  }

  @Override
  default void onMouseExit(@Nullable EventListener.Typed<MouseEvent> listener) {
    getListenerList().onMouseExit(listener);
  }

  @Override
  default void onMouseMove(@Nullable EventListener.Typed<MouseEvent> listener) {
    getListenerList().onMouseMove(listener);
  }

  @Override
  default void onAppendChild(@Nullable EventListener.Typed<MutationEvent> listener) {
    getListenerList().onAppendChild(listener);
  }

  @Override
  default void onRemoveChild(@Nullable EventListener.Typed<MutationEvent> listener) {
    getListenerList().onRemoveChild(listener);
  }

  @Override
  default void onAttributeChange(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onAttributeChange(listener);
  }

  @Override
  default void onSetAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onSetAttribute(listener);
  }

  @Override
  default void onRemoveAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onRemoveAttribute(listener);
  }

  @Override
  default void onAddAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onAddAttribute(listener);
  }

  @Override
  default void onOptionChange(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onOptionChange(listener);
  }

  @Override
  default void onSetOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onSetOption(listener);
  }

  @Override
  default void onRemoveOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onRemoveOption(listener);
  }

  @Override
  default void onAddOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    getListenerList().onAddOption(listener);
  }

  @Override
  default void onInput(@Nullable EventListener.Typed<InputEvent> listener) {
    getListenerList().onInput(listener);
  }

  @Override
  default void onLoaded(@Nullable EventListener listener) {
    getListenerList().onLoaded(listener);
  }

  @Override
  default void onSpawned(@Nullable EventListener listener) {
    getListenerList().onSpawned(listener);
  }

  @Override
  default void onClosing(@Nullable EventListener listener) {
    getListenerList().onClosing(listener);
  }

  @Override
  @Nullable
  default EventListener.Typed<MouseEvent> getOnClick() {
    return getListenerList().getOnClick();
  }

  @Override
  @Nullable
  default EventListener.Typed<MouseEvent> getOnRightClick() {
    return getListenerList().getOnRightClick();
  }

  @Override
  @Nullable
  default EventListener.Typed<MouseEvent> getOnMouseEnter() {
    return getListenerList().getOnMouseEnter();
  }

  @Override
  @Nullable
  default EventListener.Typed<MouseEvent> getOnMouseExit() {
    return getListenerList().getOnMouseExit();
  }

  @Override
  @Nullable
  default EventListener.Typed<MouseEvent> getOnMouseMove() {
    return getListenerList().getOnMouseMove();
  }

  @Override
  @Nullable
  default EventListener.Typed<MutationEvent> getOnAppendChild() {
    return getListenerList().getOnAppendChild();
  }

  @Override
  @Nullable
  default EventListener.Typed<MutationEvent> getOnRemoveChild() {
    return getListenerList().getOnRemoveChild();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnAttributeChange() {
    return getListenerList().getOnAttributeChange();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnSetAttribute() {
    return getListenerList().getOnSetAttribute();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnRemoveAttribute() {
    return getListenerList().getOnRemoveAttribute();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnAddAttribute() {
    return getListenerList().getOnAddAttribute();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnOptionChange() {
    return getListenerList().getOnOptionChange();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnSetOption() {
    return getListenerList().getOnSetOption();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnRemoveOption() {
    return getListenerList().getOnRemoveOption();
  }

  @Override
  @Nullable
  default EventListener.Typed<AttributeMutateEvent> getOnAddOption() {
    return getListenerList().getOnAddOption();
  }

  @Override
  @Nullable
  default EventListener.Typed<InputEvent> getOnInput() {
    return getListenerList().getOnInput();
  }

  @Override
  @Nullable
  default EventListener.Typed<InputEvent> getOnLoaded() {
    return getListenerList().getOnLoaded();
  }

  @Override
  @Nullable
  default EventListener getOnSpawned() {
    return getListenerList().getOnSpawned();
  }

  @Override
  @Nullable
  default EventListener getOnClosing() {
    return getListenerList().getOnClosing();
  }
}
