package net.arcadiusmc.dom.event;

public interface AttributeMutateEvent extends Event {

  String getKey();

  String getPreviousValue();

  String getNewValue();

  AttributeAction getAction();
}
