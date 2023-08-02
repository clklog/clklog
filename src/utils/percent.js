
  export function percentage(s) {
    if (s) {
      return (s * 100).toFixed(2) + "%";
    }
    return "--";
  }

  export function percent(s) {
    if (s) {
      return (s * 100).toFixed(2);
    }
    return "--";
  }