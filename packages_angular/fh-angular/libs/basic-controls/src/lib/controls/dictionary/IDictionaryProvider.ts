export abstract class IDictionaryProvider<T, N> {
  abstract dajWartosc(
    kodSlownika: string,
    kodWartosci: string,
    wersja: string
  ): Promise<T>;

  abstract dajWartoscNaDzien(
    kodSlownika: string,
    kodWartosci: string,
    dataObowiazywania: Date
  ): Promise<T>;

  abstract dajWartosci(
    kodSlownika: string,
    kodWartosciNadrzednej: string,
    dataObowiazywania: Date
  ): Promise<Array<T>>;

  abstract dajWartosciLight(
    kodSlownika: string,
    kodWartosciNadrzednej: string,
    dataObowiazywania: Date
  ): Promise<Array<N>>;

  abstract displayFunction(x: T): string;

  abstract displayExpresion(x: T, e: string): string;
}
