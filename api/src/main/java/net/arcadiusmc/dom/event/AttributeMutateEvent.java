package net.arcadiusmc.dom.event;

public interface AttributeMutateEvent extends Event {

  String getOptionKey();

  String getPreviousValue();

  String getNewValue();

  AttributeMutation getAction();
}
