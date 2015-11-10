# mdp-plan-revision

Read a more detailed description of the conceptual underpinnings in the following paper:

> [Intention Reconsideration as Metareasoning](http://www.marcvanzee.nl/publications/2015/borm2015_metareasoning.pdf) ([Marc van Zee](http://www.marcvanzee.nl), [Thomas Icard](http://stanford.edu/~icard/)), [In Bounded Optimality and Rational Metareasoning NIPS 2015 Workshop](https://sites.google.com/site/boundedoptimalityworkshop/home), 2015. 

### Screenshots



### Background: Intention Reconsideration
In this project we are interested in understanding a specific aspect of bounded optimality and metareasoning, namely the control of _plan_ or _intention reconsideration_. This problem is more circumscribed than the general problem of metareasoning, but it also inherits many of the interesting and characteristic features. The basic problem is as follows: Suppose an agent has devised a (partial) plan of action for a particular environment, as it appeared to the agent at some time _t_. But then at some later time _t'>t_---perhaps in the course of executing the plan---the agent's view on the world changes. When should the agent _replan_, and when should the agent keep its current (perhaps improvable, possibly dramatically) plan? In other words, in the specific context of a planning agent who is learning new relevant facts about the world, when should this agent stop to _rethink_, and when should it go ahead and _act_ according to its current plan? 

Our work builds on earlier, largely forgotten (regrettably, in our view) work in the _belief-desire-intention_ (BDI) agent literature, by [Kinny and Georgeff](http://www.ijcai.org/Past%20Proceedings/IJCAI-91-VOL1/PDF/014.pdf). They compare some rudimentary reconsideration strategies, as a function of several environmental parameters, in simple _Tileworld_ experiments. We reproduce their results, and also compare their reconsideration strategies to the \emph{optimal} reconsideration strategies for these environmental parameter settings. Interestingly, even the very simple agents Kinny and Georgeff considered behave nearly optimally in certain environments. However, no agent performs optimally across environments. Our results suggest that meta-meta-reasoning may indeed be called for in this setting, so that an agent might tune its reconsideration strategy flexibly to different environments.

## Implementation Details

-	The project is Eclipse, so it’s easiest to use that
-	You can run the Tileworld GUI from gui.Main
-	Tileworld settings are in settings.tileworldsettings
-	You can run a benchmark from benchmarking.TileworldBenchmark
-	Benchmark settings are in settings.benchmarksettings
-	The main simulation runs in simulations.TIleworldSimulation
-	This simulation contains a tileworld (mdp.Tileworld) and an agent.
-	The agent can be a value iteration agent, a shortest path agent, or an angelic planner (all in mdp.agent)
-	The angelic planner creates a simulation.MinimalTileworldSimulation for metareasoning, which again calls simulations.Hypothesis multiple times.


## Experiments

Experimental results and comparison with Kinny and Georgeff can be found in [our paper](http://www.marcvanzee.nl/publications/2015/borm2015_metareasoning.pdf).
