package com.zic.dilbertdaily.util;

import com.zic.dilbertdaily.data.StateEnum;

import android.graphics.Bitmap;

public class StateInfo {
	public StateEnum PreivousState;
	public StateEnum CurrentState;
	
	public StateInfo(StateEnum previousState, StateEnum currentState){
		PreivousState = previousState;
		CurrentState = currentState;
	}
}
