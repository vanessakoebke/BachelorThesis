package model;

 public record GraphStructure (
     String file,
     String category,
     int nodes,
     int edges,
     double density,
     int sccs,
     int largest_scc,
     boolean isCyclic,
     int max_width,
     int max_depth
){};