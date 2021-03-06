exception unknown_typestring_exp of string

signature GRAPH_DATA =
sig
  type psubst  (* partial substitutions *)
  type subst   (* complete (aka solved) substitution *)
  type nvdata  (* node-vertex data *)
  type edata   (* edge data *)

  val pretty_theory_name : Pretty.T

  (* A partial substitution contains a collection of constraints on pattern
   * variables. It is initialised by passing in a table containing node/edge
   * data from the pattern and target graphs. This can be used e.g. to ensure that names
   * introduced in the pattern are fresh. *)
  val init_psubst_from_data :
    nvdata VTab.T * edata ETab.T ->
    nvdata VTab.T * edata ETab.T -> psubst

  (* The match_xxx functions update a psubst or return NONE if
   * no match is possible. *)
  val match_nvdata : nvdata * nvdata -> psubst -> psubst option
  val match_edata : edata * edata -> psubst -> psubst option

  (* equality for data. used for subgraph and graph equality *)
  val nvdata_eq : nvdata * nvdata -> bool
  val edata_eq : edata * edata -> bool

  (* default vertex and edge data *)
  val default_nvdata : nvdata
  val default_edata : edata

  (* COMPAT: needed for old controller *)
  val default_nvdata_of_typestring : string -> nvdata
  (* COMPAT: needed for old controller *)
  val default_edata_of_typestring  : string -> edata

  val pretty_nvdata : nvdata -> Pretty.T
  val pretty_edata : edata -> Pretty.T

  (* For cases where node/edge data cannot be unified greedily (or uniquely),
   * this is called just before the match is finalised. One match will be
   * produced for each subst returned. In the case where this hook isn't
   * needed, let "type subst = psubst", and "solve_psubst = Seq.single". *)
  val solve_psubst : psubst -> subst Seq.seq

  (* Apply substitutions to data. *)
  val subst_in_nvdata : subst -> nvdata -> subst * nvdata
  val subst_in_edata : subst -> edata -> subst * edata
end

(* Convenience structure for defining GRAPH_DATA with empty edge data *)
structure EmptyEdgeData =
struct
  type edata = unit
  val default_edata = ()
  fun edata_eq ((),()) = true
  fun match_edata ((),()) m = SOME m
  fun subst_in_edata sub () = (sub, ())
  fun pretty_edata () = Pretty.str "()"
  fun default_edata_of_typestring _ = ()
end
