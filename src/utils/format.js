export function formatDate(date) {
  return (date.getMonth() + 1 < 10
    ? "0" + (date.getMonth() + 1)
    : date.getMonth() + 1) +
    "-" +
    date.getDate();
}
