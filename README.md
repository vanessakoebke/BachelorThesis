# Algorithms to solve Discussion-Based Semantics in Abstract Argumentation

This project is part of my bachelor thesis (due in August 2026) in Computer Science at the FernUniversität in Hagen and supervised by Dr. Kai Sauerwald.
The thesis considers aspects of the complexity, algorithmic implementation and comparative analysis for reasoning methods in abstract argumentation.

## Overview

[Abstract argumentation](https://en.wikipedia.org/wiki/Argumentation_framework) is a framework used to model and evaluate competing arguments.  
This project focuses on **discussion-based semantics**, a method that allows comparing the relative strength of arguments.

The main goal is to:

- analyze an existing **polynomial-time (PTIME)** algorithm  
- develop a **parallelizable (NC)** version of the problem  
- and compare both approaches in terms of complexity  


## Motivation

Efficient reasoning about arguments is relevant in areas like:

- artificial intelligence  
- decision support systems  
- knowledge representation  

Improving the **computational efficiency and parallelizability** of these methods helps make such systems more scalable.


## Goals

The thesis is based on recent work by [Blümel et al.](https://arxiv.org/pdf/2604.11480), who showed that certain problems can be solved in polynomial time.

Building on that, this project aims to:

- provide a **log-space reduction** for the existing reduction chain  
- show that **EQUIVDIS is in NC** (i.e. efficiently parallelizable)  
- design and describe a corresponding algorithm  
- implement the **PTIME approach vs. the NC approach**
- provide a comparative run-time analysis


## References
For more information and references please see the thesis proposal.
