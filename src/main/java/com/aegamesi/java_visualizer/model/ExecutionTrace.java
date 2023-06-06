package com.aegamesi.java_visualizer.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExecutionTrace {
	public List<Frame> frames = new ArrayList<>();
	public Map<Long, HeapEntity> heap = new TreeMap<>();
	public Map<String, Value> statics = new TreeMap<>();


	public void annotateDiffWith(ExecutionTrace previousTrace) {
		if (previousTrace == null)
			return;

		annotateDiffInFrames(previousTrace.frames);
		annotateDiffInHeap(previousTrace.heap);
	}

	public String toJsonString() {
		JSONObject obj = new JSONObject();
		obj.put("frames", frames.stream().map(Frame::toJson).toArray());
		obj.put("heap", heap.values().stream().map(HeapEntity::toJson).toArray());
		return obj.toString();
	}

	public static ExecutionTrace fromJsonString(String str) {
		JSONObject o = new JSONObject(str);
		ExecutionTrace trace = new ExecutionTrace();
		for (Object s : o.getJSONArray("frames")) {
			trace.frames.add(Frame.fromJson((JSONObject) s));
		}
		for (Object s : o.getJSONArray("heap")) {
			HeapEntity e = HeapEntity.fromJson((JSONObject) s);
			trace.heap.put(e.id, e);
		}
		return trace;
	}

	private void annotateDiffInFrames(List<Frame> previousFrames) {
		for (Frame frame : frames) {
			Frame previousFrame = previousFrames.stream()
					.filter( f -> f.getMethodName().equals(frame.getMethodName()) )
					.findFirst().orElse(null);

			if (previousFrame == null)
				continue;

			frame.annotateDiffWith(previousFrame);
		}
	}

	private void annotateDiffInHeap(Map<Long, HeapEntity> previousHeap) {
		for (Long key : heap.keySet()) {
			HeapEntity previousEntity = previousHeap.get(key);
			if (previousEntity == null)
				continue;

			HeapEntity entity = heap.get(key);
			entity.annotateDiffWith(previousEntity);
		}
	}
}
