
  export function percentage(s) {
    if (s) {
      return (s * 100).toFixed(2) + "%";
    }
    return "--";
  }
  