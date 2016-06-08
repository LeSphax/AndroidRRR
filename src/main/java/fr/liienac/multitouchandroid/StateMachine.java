/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class StateMachine<EventType, Event> {
	
	protected State current = null;
	protected State first = null;

	public void handleEvent(EventType type, Event evt) {
		current.handleEvent(type, evt);
	}
	
	private void goTo(State s) {
		current.leave();
		current = s;
		current.enter();
	}	

	public class State {

		//Map<EventType, Vector<Transition> > transitionsPerType = new HashMap<EventType, Vector<Transition>>(); // no static type checking
        Map<Object, Vector<Transition> > transitionsPerType = new HashMap<Object, Vector<Transition>>(); // with static type checking
		
		public State() {
			// first state is the initial state
			if (first==null) { first = this; current = first; }
		}				
		//public State(boolean notfirst) {}
		
		protected void enter() {}
		protected void leave() {}

        //public class Transition { // no static type checking
		public class Transition<EventT> { // with static type checking
			//public Transition(EventType eventType) {
            public Transition(Class<EventT> clazz) {
				// register transition in state
				Transition t = this;
				//Vector<Transition> ts = transitionsPerType.get(eventType);
                Vector<Transition> ts = transitionsPerType.get(clazz);
				if (ts==null) {
					ts=new Vector<Transition>();
					ts.add(t);
					//transitionsPerType.put(eventType, ts);
                    transitionsPerType.put(clazz, ts);
				} else {
					ts.add(t);
				}
			}
			protected boolean guard(EventT evt) { return true; }
			protected void action(EventT evt) {}
			protected State goTo() { return current; }
		}

		protected void handleEvent(EventType type, Event evt) {
			Vector<Transition> ts = transitionsPerType.get(type);
			if (ts==null) { return; }
			for (Transition t : ts) {
				if(t.guard(evt)) {
					t.action(evt);
					StateMachine.this.goTo(t.goTo());
					break;
				}
			}					
		}	
	}

	//public State finished = new State(true) {};
	//public boolean isFinished() { return current==finished; }
}


